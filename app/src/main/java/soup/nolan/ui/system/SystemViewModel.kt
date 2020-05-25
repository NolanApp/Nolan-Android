package soup.nolan.ui.system

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.window.DeviceState
import timber.log.Timber

class SystemViewModel : ViewModel() {

    private val _isHalfOpened = MutableLiveData<Boolean>()
    val isHalfOpened: LiveData<Boolean>
        get() = _isHalfOpened

    fun onDeviceStateChanged(deviceState: DeviceState) {
        Timber.d("onDeviceStateChanged: $deviceState")
        if (deviceState.posture == DeviceState.POSTURE_UNKNOWN) {
            return
        }
        if (_isHalfOpened.value != deviceState.isHalfOpened) {
            _isHalfOpened.postValue(deviceState.isHalfOpened)
        }
    }

    private val DeviceState.isHalfOpened: Boolean
        get() = posture == DeviceState.POSTURE_HALF_OPENED
                || posture == DeviceState.POSTURE_CLOSED
}
