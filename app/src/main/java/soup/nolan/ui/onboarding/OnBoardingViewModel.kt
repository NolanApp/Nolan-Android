package soup.nolan.ui.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import soup.nolan.data.CameraFilterRepository
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData

class OnBoardingViewModel @ViewModelInject constructor(
    repository: CameraFilterRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _allowEvent = MutableEventLiveData<Unit>()
    val allowEvent: EventLiveData<Unit>
        get() = _allowEvent

    private val _navigationEvent = MutableEventLiveData<Unit>()
    val navigationEvent: EventLiveData<Unit>
        get() = _navigationEvent

    init {
        repository.generateFilterThumbnailsIfNeeded()
    }

    fun onAllowClick() {
        _allowEvent.event = Unit
    }

    fun onPermissionGranted() {
        appSettings.showOnBoarding = false
        _navigationEvent.event = Unit
    }
}
