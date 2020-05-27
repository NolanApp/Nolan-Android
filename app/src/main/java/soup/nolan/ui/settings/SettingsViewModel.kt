package soup.nolan.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import soup.nolan.Dependency
import soup.nolan.data.PlayRepository

class SettingsViewModel(
    private val repository: PlayRepository = Dependency.playRepository
) : ViewModel() {

    val latestVersionCode: LiveData<Int> = liveData {
        emit(repository.getAvailableVersionCode())
    }
}
