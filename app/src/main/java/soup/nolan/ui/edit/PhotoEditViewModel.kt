package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.collection.LruCache
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.factory.ImageFactory
import soup.nolan.factory.ShareUriFactory
import soup.nolan.filter.stylize.LegacyStyleInput
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.model.CameraFilter
import soup.nolan.settings.AppSettings
import soup.nolan.stylize.common.NoStyleInput
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.camera.filter.CameraFilterViewModelDelegate
import soup.nolan.ui.share.ShareItemUiModel
import soup.nolan.ui.utils.setValueIfNew
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class PhotoEditViewModel @Inject constructor(
    viewModelDelegate: CameraFilterViewModelDelegate,
    private val imageFactory: ImageFactory,
    private val shareUriFactory: ShareUriFactory,
    private val styleTransfer: LegacyStyleTransfer,
    private val appSettings: AppSettings
) : ViewModel(), CameraFilterViewModelDelegate by viewModelDelegate {

    private var isEnterAnimationDone: Boolean = false
    private var wasLoading: Boolean = false

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isZoomIn = MutableLiveData<Boolean>(false)
    val isZoomIn: LiveData<Boolean>
        get() = _isZoomIn

    private val _isShutterVisible = MutableLiveData(true)
    val isShutterVisible: LiveData<Boolean>
        get() = _isShutterVisible

    private val _buttonPanelIsShown = MutableLiveData(false)
    val buttonPanelIsShown: LiveData<Boolean>
        get() = _buttonPanelIsShown

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap>
        get() = _bitmap

    private val _uiEvent = MutableEventLiveData<PhotoEditUiEvent>()
    val uiEvent: EventLiveData<PhotoEditUiEvent>
        get() = _uiEvent

    private val memoryCache: LruCache<String, Bitmap>
    private var originImageUri: Uri? = null
    private var lastImageUri: Uri? = null
    private var lastCropRect: Rect? = null

    init {
        val imageSize = styleTransfer.getMaxImageSize()
        val maxMemory = (Runtime.getRuntime().maxMemory() / imageSize).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / imageSize
            }
        }
    }

    override fun onCleared() {
        memoryCache.evictAll()
        super.onCleared()
    }

    fun init(imageUri: Uri) {
        if (originImageUri != null) return
        originImageUri = imageUri
        appSettings.photoEditCount += 1
        update(imageUri, null)
    }

    fun changeFilter(filter: CameraFilter) {
        val imageUri = lastImageUri ?: return
        viewModelScope.launch {
            val bitmap = imageFactory.getBitmap(imageUri, styleTransfer.getMaxImageSize())
            updateInternal(bitmap, filter)
        }
    }

    fun update(imageUri: Uri, cropRect: Rect?) {
        memoryCache.evictAll()
        lastImageUri = imageUri
        lastCropRect = cropRect

        viewModelScope.launch {
            val bitmap = imageFactory.getBitmap(imageUri, styleTransfer.getMaxImageSize())
            _bitmap.value = bitmap
            updateInternal(bitmap, getSelectedFilter() ?: CameraFilter.default)
        }
    }

    private suspend fun updateInternal(bitmap: Bitmap, filter: CameraFilter) {
        val cacheBitmap = memoryCache.get(filter.id)
        if (cacheBitmap != null) {
            _bitmap.value = cacheBitmap
            _buttonPanelIsShown.value = true
            return
        }

        try {
            _buttonPanelIsShown.value = false
            onInProcessing()

            val duration = measureTimeMillis {
                _bitmap.value = if (appSettings.showWatermark) {
                    imageFactory.withWatermark(bitmap.stylized(filter))
                } else {
                    bitmap.stylized(filter)
                }
            }
            Timber.d("success: $duration ms")
            if (BuildConfig.DEBUG) {
                _uiEvent.event = PhotoEditUiEvent.ShowToast("Success! ($duration ms)")
            }
        } catch (e: Exception) {
            Timber.w("failure: $e")
            _uiEvent.event = PhotoEditUiEvent.ShowErrorToast(R.string.photo_edit_error_unknown)
        } finally {
            _buttonPanelIsShown.value = true
            onProcessDone()
        }
    }

    fun onCropClick() {
        originImageUri?.let {
            _uiEvent.event = PhotoEditUiEvent.GoToCrop(it, lastCropRect)
        }
    }

    fun onSaveClick() {
        _bitmap.value?.let {
            _uiEvent.event = PhotoEditUiEvent.Save(it)
        }
    }

    fun onShareClick() {
        _uiEvent.event = PhotoEditUiEvent.ShowShare
    }

    fun onShareItemClick(
        uiModel: ShareItemUiModel,
        drawable: Drawable
    ) {
        viewModelScope.launch {
            val shareImageUri = shareUriFactory.createShareImageUri(
                drawable,
                appSettings.showWatermark
            )
            if (shareImageUri == null) {
                _uiEvent.event = PhotoEditUiEvent.ShowErrorToast(R.string.photo_edit_error_unknown)
            } else {
                _uiEvent.event = PhotoEditUiEvent.Share(uiModel, shareImageUri)
            }
        }
    }

    private suspend fun Bitmap.stylized(filter: CameraFilter): Bitmap {
        return when (filter.input) {
            is NoStyleInput -> {
                this
            }
            is LegacyStyleInput -> {
                styleTransfer.transform(this, filter.input).also {
                    memoryCache.put(filter.id, it)
                }
            }
            else -> {
                throw IllegalStateException("Invalid input(id=${filter.id})!")
            }
        }
    }

    fun onEnterAnimationDone() {
        Timber.d("onEnterAnimationDone: wasLoading=$wasLoading")
        isEnterAnimationDone = true
        _isLoading.setValueIfNew(wasLoading)
        _isShutterVisible.value = false
    }

    fun onZoomChanged(zoomIn: Boolean) {
        _isZoomIn.setValueIfNew(zoomIn)
    }

    private fun onInProcessing() {
        Timber.d("onInProcessing: ")
        wasLoading = true
        if (isEnterAnimationDone) {
            _isLoading.value = true
        }
    }

    private fun onProcessDone() {
        Timber.d("onProcessDone: ")
        wasLoading = false
        if (isEnterAnimationDone) {
            _isLoading.value = false
        }
    }
}
