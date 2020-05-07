package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import soup.nolan.BuildConfig
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.model.CameraFilter
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.base.BaseViewModel
import soup.nolan.ui.utils.ImageFactory
import timber.log.Timber
import javax.inject.Inject

class PhotoEditViewModel @Inject constructor(
    private val imageFactory: ImageFactory,
    private val styleTransfer: LegacyStyleTransfer,
    private val appSettings: AppSettings
) : BaseViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap>
        get() = _bitmap

    private val _uiEvent = MutableEventLiveData<PhotoEditUiEvent>()
    val uiEvent: EventLiveData<PhotoEditUiEvent>
        get() = _uiEvent

    private var originImageUri: Uri? = null
    private var lastImageUri: Uri? = null
    private var lastCropRect: Rect? = null

    fun init(fileUri: Uri) {
        if (originImageUri != null) return
        originImageUri = fileUri
        update(fileUri, null)
    }

    fun changeFilter(filter: CameraFilter) {
        val imageUri = lastImageUri ?: return
        updateInternal(imageUri, filter)
    }

    fun update(imageUri: Uri, cropRect: Rect?) {
        lastImageUri = imageUri
        lastCropRect = cropRect
        updateInternal(imageUri, getSelectedCameraFilter())
    }

    private fun updateInternal(imageUri: Uri, filter: CameraFilter) {
        val bitmap = imageFactory.getBitmap(imageUri)
        if (_bitmap.value == null) {
            _bitmap.value = bitmap
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val start = System.currentTimeMillis()
                val styleBitmap = withContext(Dispatchers.IO) {
                    styleTransfer.transform(bitmap, filter.input)
                }
                val duration = System.currentTimeMillis() - start
                Timber.d("success: $duration ms")
                if (BuildConfig.DEBUG) {
                    _uiEvent.event = PhotoEditUiEvent.ShowToast("Success! ($duration ms)")
                }
                _bitmap.value = styleBitmap
            } catch (e: Exception) {
                Timber.w("failure: $e")
            } finally {
                _isLoading.value = false
            }
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
        _bitmap.value?.let {
            _uiEvent.event = PhotoEditUiEvent.ShowShare(it)
        }
    }

    private fun getSelectedCameraFilter(): CameraFilter {
        return CameraFilter.all()
            .firstOrNull { it.id == appSettings.lastFilterId }
            ?: CameraFilter.A25
    }
}
