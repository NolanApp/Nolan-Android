package soup.nolan.ui.camera.filter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import soup.nolan.data.CameraFilterRepository
import soup.nolan.model.CameraFilter
import soup.nolan.model.VisualCameraFilter
import soup.nolan.settings.AppSettings
import timber.log.Timber

class CameraFilterViewModel @ViewModelInject constructor(
    private val repository: CameraFilterRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    val filterList: LiveData<CameraFilterUiModel> =
        repository.getAllVisualFiltersLiveData().map {
            CameraFilterUiModel(it)
        }

    private val _selectedPosition = MutableLiveData<Int>()
    val selectedPosition: LiveData<Int>
        get() = _selectedPosition

    init {
        notifyListChanged(appSettings.lastFilterId ?: CameraFilter.default.id)
    }

    fun onFilterSelect(item: VisualCameraFilter) {
        if (appSettings.lastFilterId == item.id) {
            Timber.w("onFilterSelect: ${item.id} is already selected!")
            return
        }
        appSettings.lastFilterId = item.id
        notifyListChanged(item.id)
    }

    private fun notifyListChanged(selectedFilterId: String) {
        _selectedPosition.value = repository.getAllFilters()
            .indexOfFirst { it.id == selectedFilterId }
    }
}
