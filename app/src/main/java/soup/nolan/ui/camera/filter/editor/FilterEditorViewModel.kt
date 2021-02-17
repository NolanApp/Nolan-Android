package soup.nolan.ui.camera.filter.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import soup.nolan.factory.ImageStore
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.camera.filter.CameraFilterViewModelDelegate
import javax.inject.Inject

@HiltViewModel
class FilterEditorViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val imageStore: ImageStore,
    viewModelDelegate: CameraFilterViewModelDelegate
) : ViewModel(), CameraFilterViewModelDelegate by viewModelDelegate {

    val header: LiveData<FilterEditorHeaderUiModel> =
        originalImageUri.map {
            FilterEditorHeaderUiModel(it)
        }

    val list: LiveData<List<FilterEditorItemUiModel>> = selectedFilter.switchMap { selectedFilter ->
        allVisualFiltersLiveData.map {
            it.map { filter ->
                FilterEditorItemUiModel(filter, isSelected = filter.id == selectedFilter.id)
            }
        }
    }

    private val _uiEvent = MutableEventLiveData<FilterEditorUiEvent>()
    val uiEvent: EventLiveData<FilterEditorUiEvent>
        get() = _uiEvent

    fun onItemClick(uiModel: FilterEditorItemUiModel) {
        onFilterSelected(uiModel.filter)
    }

    fun onDefaultClick() {
        onOriginImageChanged(imageStore.getDefaultImageUri())
    }

    fun onCameraClick() {
        _uiEvent.event = FilterEditorUiEvent.TakePicture(
            imageStore.createCameraImageUri()
        )
    }

    fun onAlbumClick() {
        _uiEvent.event = FilterEditorUiEvent.PickFromAlbum
    }

    fun onStartClick() {
        appSettings.showFilterEditor = false
        _uiEvent.event = FilterEditorUiEvent.GoToCamera
    }
}
