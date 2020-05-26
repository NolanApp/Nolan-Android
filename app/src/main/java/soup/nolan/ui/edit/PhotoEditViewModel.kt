package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import soup.nolan.BuildConfig
import soup.nolan.Dependency
import soup.nolan.R
import soup.nolan.filter.stylize.LegacyStyleInput
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.model.CameraFilter
import soup.nolan.settings.AppSettings
import soup.nolan.stylize.common.NoStyleInput
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.utils.ImageFactory
import timber.log.Timber

class PhotoEditViewModel(
    private val imageFactory: ImageFactory = Dependency.imageFactory,
    private val styleTransfer: LegacyStyleTransfer = Dependency.styleTransfer,
    private val appSettings: AppSettings = Dependency.appSettings
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _buttonPanelIsShown = MutableLiveData(false)
    val buttonPanelIsShown: LiveData<Boolean>
        get() = _buttonPanelIsShown

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap>
        get() = _bitmap

    private val _uiEvent = MutableEventLiveData<PhotoEditUiEvent>()
    val uiEvent: EventLiveData<PhotoEditUiEvent>
        get() = _uiEvent

    private var originImageUri: Uri? = null
    private var lastImageUri: Uri? = null
    private var lastCropRect: Rect? = null

    fun init(imageUri: Uri) {
        if (originImageUri != null) return
        originImageUri = imageUri
        update(imageUri, null)
    }

    fun changeFilter(filter: CameraFilter) {
        val imageUri = lastImageUri ?: return
        updateInternal(imageFactory.getBitmap(imageUri), filter)
    }

    fun update(imageUri: Uri, cropRect: Rect?) {
        lastImageUri = imageUri
        lastCropRect = cropRect

        imageFactory.getBitmap(imageUri).let {
            _bitmap.value = it
            updateInternal(it, getSelectedCameraFilter())
        }
    }

    private fun updateInternal(bitmap: Bitmap, filter: CameraFilter) {
        val input = filter.input
        if (input is NoStyleInput) {
            _bitmap.value = bitmap
            _buttonPanelIsShown.value = true
            return
        }
        if (input !is LegacyStyleInput) {
            Timber.d("updateInternal: input($input) is invalid!")
            _buttonPanelIsShown.value = true
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _buttonPanelIsShown.value = false
            try {
                val start = System.currentTimeMillis()
                val styleBitmap = styleTransfer.transform(bitmap, input)
                val duration = System.currentTimeMillis() - start
                Timber.d("success: $duration ms")
                if (BuildConfig.DEBUG) {
                    _uiEvent.event = PhotoEditUiEvent.ShowToast("Success! ($duration ms)")
                }
                _bitmap.value = styleBitmap
            } catch (e: Exception) {
                Timber.w("failure: $e")
                _uiEvent.event = PhotoEditUiEvent.ShowErrorToast(R.string.photo_edit_error_unknown)
            } finally {
                _buttonPanelIsShown.value = true
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
