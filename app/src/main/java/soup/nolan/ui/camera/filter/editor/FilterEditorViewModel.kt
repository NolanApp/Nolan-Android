package soup.nolan.ui.camera.filter.editor

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import soup.nolan.factory.ImageStore
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.camera.filter.CameraFilterViewModelDelegate
import soup.nolan.ui.utils.postValueIfNew

class FilterEditorViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val appSettings: AppSettings,
    private val imageStore: ImageStore,
    viewModelDelegate: CameraFilterViewModelDelegate
) : ViewModel(), CameraFilterViewModelDelegate by viewModelDelegate {

    private var savedSelectedId: String?
        get() = savedState.get(KEY_SELECTED_ID) ?: appSettings.lastFilterId
        set(value) {
            savedState.set(KEY_SELECTED_ID, value)
            if (value != null) {
                _selectedId.postValue(value)
            }
            _canDone.postValueIfNew(value != null)
        }

    val header: LiveData<FilterEditorHeaderUiModel> =
        originalImageUri.map {
            FilterEditorHeaderUiModel(it)
        }

    private val _selectedId = MutableLiveData<String>(savedSelectedId)
    val list: LiveData<List<FilterEditorItemUiModel>> = _selectedId.switchMap { selectedId ->
        allVisualFiltersLiveData.map {
            it.map { filter ->
                FilterEditorItemUiModel(filter, isSelected = filter.id == selectedId)
            }
        }
    }

    private val _canDone = MutableLiveData<Boolean>(savedSelectedId != null)
    val canDone: LiveData<Boolean>
        get() = _canDone

    private val _uiEvent = MutableEventLiveData<FilterEditorUiEvent>()
    val uiEvent: EventLiveData<FilterEditorUiEvent>
        get() = _uiEvent

    fun onItemClick(uiModel: FilterEditorItemUiModel) {
        savedSelectedId = uiModel.filter.id
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
        savedSelectedId?.let {
            appSettings.lastFilterId = it
            appSettings.showFilterEditor = false
            _uiEvent.event = FilterEditorUiEvent.GoToCamera
        }
    }

    companion object {
        private const val KEY_SELECTED_ID = "selected_id"
    }
}
