package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import soup.nolan.BuildConfig
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.base.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class PhotoEditViewModel @Inject constructor(
    private val styleTransfer: LegacyStyleTransfer
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

    private var isNotInitialized: Boolean = true
    private var originImageUri: Uri? = null
    private var lastImageUri: Uri? = null

    fun init(imageUri: Uri, image: Bitmap, fromGallery: Boolean) {
        if (isNotInitialized && fromGallery) {
            isNotInitialized = false
            _uiEvent.event = PhotoEditUiEvent.GoToCrop(imageUri)
            return
        }

        isNotInitialized = false
        originImageUri = imageUri
        _bitmap.value = image

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val start = System.currentTimeMillis()
                val it = styleTransfer.transform(image)
                val duration = System.currentTimeMillis() - start
                Timber.d("success: $it $duration ms")

                _bitmap.value = it

                if (BuildConfig.DEBUG) {
                    _uiEvent.event = PhotoEditUiEvent.ShowToast("Success! ($duration ms)")
                }
            } catch (e: Exception) {
                Timber.w("failure: $e")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSaveClick() {
        _bitmap.value?.let {
            _uiEvent.event = PhotoEditUiEvent.Save(it)
        }
    }

    fun onShareClick() {
        lastImageUri?.let {
            _uiEvent.event = PhotoEditUiEvent.GoToShare(it)
        }
    }
}
