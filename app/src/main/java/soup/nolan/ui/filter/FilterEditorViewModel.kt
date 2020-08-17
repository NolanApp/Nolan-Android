package soup.nolan.ui.filter

import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import soup.nolan.data.CameraFilterRepository
import soup.nolan.factory.ImageUriFactory
import soup.nolan.model.CameraFilter
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData

class FilterEditorViewModel @ViewModelInject constructor(
    private val appSettings: AppSettings,
    private val repository: CameraFilterRepository,
    private val imageUriFactory: ImageUriFactory,
    @Assisted private val savedState: SavedStateHandle
) : ViewModel() {

    private val _uiModel = MutableLiveData<List<FilterEditorUiModel>>()
    val uiModel: LiveData<List<FilterEditorUiModel>>
        get() = _uiModel

    private val _canStart = MutableLiveData<Boolean>()
    val canStart: LiveData<Boolean>
        get() = _canStart

    private val _uiEvent = MutableEventLiveData<FilterEditorUiEvent>()
    val uiEvent: EventLiveData<FilterEditorUiEvent>
        get() = _uiEvent

    private var savedSelectedId: String?
        set(value) = savedState.set(KEY_SELECTED_ID, value)
        get() = savedState.get(KEY_SELECTED_ID)

    init {
        updateUiModel(uri = imageUriFactory.getDefaultImageUri())
        updateCanStart()
    }

    fun onOriginImageChanged(uri: Uri) {
        //TODO:
        updateUiModel(uri = uri)
        repository.fetchOriginalUri(uri)
    }

    fun onItemClick(uiModel: FilterEditorUiModel.Item) {
        savedSelectedId = uiModel.filter.id
        updateUiModel(uri = imageUriFactory.getDefaultImageUri(), selectedId = uiModel.filter.id)
        updateCanStart(true)
    }

    fun onCameraClick() {
        _uiEvent.event = FilterEditorUiEvent.TakePicture(imageUriFactory.createCameraImageUri())
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

    private fun updateUiModel(uri: Uri, selectedId: String? = savedSelectedId) {
        _uiModel.value = mutableListOf<FilterEditorUiModel>().apply {
            add(FilterEditorUiModel.Header(uri))
            addAll(CameraFilter.all().map { filter ->
                FilterEditorUiModel.Item(
                    filter = filter,
                    imageUri = imageUriFactory.getFilterImageUri(filter),
                    isSelected = filter.id == selectedId
                )
            })
        }
    }

    private fun updateCanStart(canStart: Boolean = savedSelectedId != null) {
        _canStart.value = canStart
    }

    companion object {
        private const val KEY_SELECTED_ID = "selected_id"
    }
}
