package soup.nolan.ui.system

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.DeviceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.Dependency
import soup.nolan.model.Appearance
import soup.nolan.settings.AppSettings
import soup.nolan.ui.utils.postValueIfNew
import timber.log.Timber

class SystemViewModel(
    private val appSettings: AppSettings = Dependency.appSettings
) : ViewModel() {

    private val _currentAppearance = MutableLiveData<Appearance>()
    val currentAppearance: LiveData<Appearance>
        get() = _currentAppearance

    private val _isHalfOpened = MutableLiveData<Boolean>()
    val isHalfOpened: LiveData<Boolean>
        get() = _isHalfOpened

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _currentAppearance.postValue(Appearance.of(appSettings.currentAppearance))
        }
    }

    fun onDeviceStateChanged(deviceState: DeviceState) {
        Timber.d("onDeviceStateChanged: $deviceState")
        if (deviceState.posture == DeviceState.POSTURE_UNKNOWN) {
            return
        }
        val isHalfOpened = when (deviceState.posture) {
            DeviceState.POSTURE_HALF_OPENED,
            DeviceState.POSTURE_CLOSED -> true
            else -> false
        }
        _isHalfOpened.postValueIfNew(isHalfOpened)
    }

    fun onAppearanceChanged(appearance: Appearance) {
        if (_currentAppearance.value == appearance) return
        _currentAppearance.value = appearance
        appSettings.currentAppearance = appearance.value
        AppCompatDelegate.setDefaultNightMode(appearance.nightMode)
    }
}
