package soup.nolan.ui.camera

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.camera.filter.CameraFilterViewModelDelegate

class CameraViewModel @ViewModelInject constructor(
    viewModelDelegate: CameraFilterViewModelDelegate,
    private val appSettings: AppSettings
) : ViewModel(), CameraFilterViewModelDelegate by viewModelDelegate {

    private val _lensFacingFront = MutableLiveData(appSettings.lensFacingFront)
    val lensFacingFront: LiveData<Boolean>
        get() = _lensFacingFront

    private val _uiEvent = MutableEventLiveData<CameraUiEvent>()
    val uiEvent: EventLiveData<CameraUiEvent>
        get() = _uiEvent

    fun onLensFacingClick(lensFacingFront: Boolean) {
        appSettings.lensFacingFront = lensFacingFront.not()
        _lensFacingFront.value = lensFacingFront.not()
    }

    fun onGalleryButtonClick() {
        _uiEvent.event = CameraUiEvent.GoToGallery
    }
}
