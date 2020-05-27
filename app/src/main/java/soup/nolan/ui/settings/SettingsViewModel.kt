package soup.nolan.ui.settings

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.Dependency
import soup.nolan.data.PlayRepository
import soup.nolan.settings.AppSettings

class SettingsViewModel(
    private val repository: PlayRepository = Dependency.playRepository,
    private val appSettings: AppSettings = Dependency.appSettings
) : ViewModel() {

    private val _showWatermark = MutableLiveData<Boolean>()
    val showWatermark: LiveData<Boolean>
        get() = _showWatermark

    val latestVersionCode: LiveData<Int> = liveData {
        emit(repository.getAvailableVersionCode())
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _showWatermark.postValue(appSettings.showWatermark)
        }
    }

    fun onShowWatermarkChecked(checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettings.showWatermark = checked
            _showWatermark.postValue(checked)
        }
    }
}
