package soup.nolan.ui.filter

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import soup.nolan.model.CameraFilter
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData

class FilterEditorViewModel(
    private val savedState: SavedStateHandle
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
        updateUiModel(uri = null)
        updateCanStart()
    }

    fun onOriginImageChanged(uri: Uri) {
        updateUiModel(uri = uri)
    }

    fun onItemClick(uiModel: FilterEditorUiModel.Item) {
        savedSelectedId = uiModel.filter.id
        updateUiModel(uri = null, selectedId = uiModel.filter.id)
        updateCanStart(true)
    }

    fun onCameraClick() {
        _uiEvent.event = FilterEditorUiEvent.CameraPicker
    }

    fun onAlbumClick() {
        _uiEvent.event = FilterEditorUiEvent.AlbumPicker
    }

    private fun updateUiModel(uri: Uri?, selectedId: String? = savedSelectedId) {
        _uiModel.value = mutableListOf<FilterEditorUiModel>().apply {
            add(FilterEditorUiModel.Header(uri))
            addAll(CameraFilter.all().map {
                FilterEditorUiModel.Item(it, isSelected = it.id == selectedId)
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
