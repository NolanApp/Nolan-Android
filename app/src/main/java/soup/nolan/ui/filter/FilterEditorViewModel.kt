package soup.nolan.ui.filter

import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import soup.nolan.data.CameraFilterRepository
import soup.nolan.factory.ImageStore
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.utils.postValueIfNew

class FilterEditorViewModel @ViewModelInject constructor(
    @Assisted private val savedState: SavedStateHandle,
    private val repository: CameraFilterRepository,
    private val appSettings: AppSettings,
    private val imageStore: ImageStore
) : ViewModel() {

    private var savedSelectedId: String?
        get() = savedState.get(KEY_SELECTED_ID)
        set(value) {
            savedState.set(KEY_SELECTED_ID, value)
            updateList(savedSelectedId)
            _canStart.postValueIfNew(value != null)
        }

    private val _originalUri = MutableLiveData<Uri>(
        imageStore.getOriginalImageUri()
            ?: imageStore.getDefaultImageUri()
    )
    val header: LiveData<FilterEditorHeaderUiModel> = _originalUri.map {
        FilterEditorHeaderUiModel(it)
    }

    private val _list = MutableLiveData<List<FilterEditorItemUiModel>>()
    val list: LiveData<List<FilterEditorItemUiModel>>
        get() = _list

    private val _canStart = MutableLiveData<Boolean>(savedSelectedId != null)
    val canStart: LiveData<Boolean>
        get() = _canStart

    private val _uiEvent = MutableEventLiveData<FilterEditorUiEvent>()
    val uiEvent: EventLiveData<FilterEditorUiEvent>
        get() = _uiEvent

    init {
        updateList(savedSelectedId)
    }

    fun onOriginImageChanged(uri: Uri) {
        _originalUri.value = uri
        repository.updateFilterImages(uri)
    }

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
            _uiEvent.event = FilterEditorUiEvent.GoToCamera
        }
    }

    private fun updateList(selectedId: String?) {
        _list.value = repository.getAllCameraFilterList()
            .map { filter ->
                FilterEditorItemUiModel(
                    filter = filter,
                    imageUri = imageStore.getFilterImageUri(filter),
                    isSelected = filter.id == selectedId
                )
            }
    }

    companion object {
        private const val KEY_SELECTED_ID = "selected_id"
    }
}
