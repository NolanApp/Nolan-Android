package soup.nolan.ui.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _navigationEvent = MutableEventLiveData<SplashUiEvent>()
    val navigationEvent: EventLiveData<SplashUiEvent>
        get() = _navigationEvent

    fun onAnimationEnd(hasRequiredPermissions: Boolean) {
        _navigationEvent.event = when {
            appSettings.showOnBoarding -> SplashUiEvent.GoToOnBoarding
            hasRequiredPermissions.not() -> SplashUiEvent.GoToPermission
            appSettings.showFilterEditor -> SplashUiEvent.GoToFilterEditor
            else -> SplashUiEvent.GoToCamera
        }
    }
}

enum class SplashUiEvent {
    GoToOnBoarding,
    GoToPermission, GoToFilterEditor, GoToCamera
}