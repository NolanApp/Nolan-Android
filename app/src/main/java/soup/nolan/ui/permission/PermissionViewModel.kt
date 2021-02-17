package soup.nolan.ui.permission

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
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
