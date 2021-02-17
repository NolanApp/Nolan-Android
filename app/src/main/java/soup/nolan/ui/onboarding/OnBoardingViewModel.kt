package soup.nolan.ui.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.camera.filter.CameraFilterViewModelDelegate
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    viewModelDelegate: CameraFilterViewModelDelegate,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _allowEvent = MutableEventLiveData<Unit>()
    val allowEvent: EventLiveData<Unit>
        get() = _allowEvent

    private val _navigationEvent = MutableEventLiveData<Unit>()
    val navigationEvent: EventLiveData<Unit>
        get() = _navigationEvent

    init {
        viewModelDelegate.generateFilterThumbnailsIfNeeded()
    }

    fun onAllowClick() {
        _allowEvent.event = Unit
    }

    fun onPermissionGranted() {
        appSettings.showOnBoarding = false
        _navigationEvent.event = Unit
    }
}
