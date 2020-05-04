package soup.nolan.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import soup.nolan.settings.AppSettings
import soup.nolan.ui.base.BaseViewModel
import javax.inject.Inject

class CameraViewModel @Inject constructor(
    private val appSettings: AppSettings
) : BaseViewModel() {

    private val _lensFacingFront = MutableLiveData(appSettings.lensFacingFront)
    val lensFacingFront: LiveData<Boolean>
        get() = _lensFacingFront

    fun onLensFacingClick(lensFacingFront: Boolean) {
        appSettings.lensFacingFront = lensFacingFront.not()
        _lensFacingFront.value = lensFacingFront.not()
    }
}
