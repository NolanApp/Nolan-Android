package soup.nolan.ui.camera.filter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import soup.nolan.data.CameraFilterRepository
import soup.nolan.factory.ImageStore
import soup.nolan.settings.AppSettings
import timber.log.Timber

class CameraFilterViewModel @ViewModelInject constructor(
    private val repository: CameraFilterRepository,
    private val appSettings: AppSettings,
    private val imageStore: ImageStore
) : ViewModel() {

    private val _filterList = MutableLiveData<CameraFilterUiModel>()
    val filterList: LiveData<CameraFilterUiModel>
        get() = _filterList

    private val _selectedPosition = MutableLiveData<Int>()
    val selectedPosition: LiveData<Int>
        get() = _selectedPosition

    init {
        _filterList.value = repository.getAllCameraFilterList()
            .map { CameraFilterItemUiModel(it, imageStore.getFilterImageUri(it)) }
            .let { CameraFilterUiModel(it) }
        notifyListChanged(appSettings.lastFilterId)
    }

    fun onFilterSelect(item: CameraFilterItemUiModel) {
        if (appSettings.lastFilterId == item.id) {
            Timber.w("onFilterSelect: ${item.id} is already selected!")
            return
        }
        appSettings.lastFilterId = item.id
        notifyListChanged(item.id)
    }

    private fun notifyListChanged(selectedFilterId: String) {
        _selectedPosition.value = repository.getAllCameraFilterList()
            .indexOfFirst { it.id == selectedFilterId }
    }
}
