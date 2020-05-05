package soup.nolan.ui.camera.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import soup.nolan.model.CameraFilter
import soup.nolan.settings.AppSettings
import timber.log.Timber
import javax.inject.Inject

class CameraFilterViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _filterList = MutableLiveData<CameraFilterUiModel>()
    val filterList: LiveData<CameraFilterUiModel>
        get() = _filterList

    private val _selectedPosition = MutableLiveData<Int>()
    val selectedPosition: LiveData<Int>
        get() = _selectedPosition

    init {
        _filterList.value = CameraFilter.all()
            .map { CameraFilterItemUiModel(it.id, it.input, it.getThumbnailResId()) }
            .let { CameraFilterUiModel(it) }
        notifyListChanged(appSettings.lastFilterId)
    }

    fun onFilterSelect(filter: CameraFilterItemUiModel) {
        if (appSettings.lastFilterId == filter.id) {
            Timber.w("onFilterSelect: ${filter.id} is already selected!")
            return
        }
        appSettings.lastFilterId = filter.id
        notifyListChanged(filter.id)
    }

    private fun notifyListChanged(selectedFilterId: String) {
        _selectedPosition.value = CameraFilter.all().indexOfFirst { it.id == selectedFilterId }
    }
}
