package soup.nolan.ui.permission

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData

class PermissionViewModel @ViewModelInject constructor(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _navigationEvent = MutableEventLiveData<PermissionUiEvent>()
    val navigationEvent: EventLiveData<PermissionUiEvent>
        get() = _navigationEvent

    fun onPermissionGranted() {
        _navigationEvent.event = if (appSettings.showFilterEditor) {
            PermissionUiEvent.GoToFilterEditor
        } else {
            PermissionUiEvent.GoToCamera
        }
    }
}
