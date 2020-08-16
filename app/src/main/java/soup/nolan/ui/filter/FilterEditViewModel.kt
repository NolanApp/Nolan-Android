package soup.nolan.ui.filter

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import soup.nolan.model.CameraFilter

class FilterEditViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    private val _uiModel = MutableLiveData<List<FilterEditUiModel>>()
    val uiModel: LiveData<List<FilterEditUiModel>>
        get() = _uiModel

    private val _canStart = MutableLiveData<Boolean>()
    val canStart: LiveData<Boolean>
        get() = _canStart

    init {
        val selectedId: String? = savedState.get(KEY_SELECTED_ID)
        updateUiModel(selectedId)
        updateCanStart(selectedId != null)
    }

    fun onItemClick(uiModel: FilterEditUiModel.Item) {
        savedState.set(KEY_SELECTED_ID, uiModel.filter.id)
        updateUiModel(uiModel.filter.id)
        updateCanStart(true)
    }

    private fun updateUiModel(selectedId: String?) {
        _uiModel.value = mutableListOf<FilterEditUiModel>().apply {
            add(FilterEditUiModel.Header(null))
            addAll(CameraFilter.all().map {
                FilterEditUiModel.Item(it, isSelected = it.id == selectedId)
            })
        }
    }

    private fun updateCanStart(canStart: Boolean) {
        _canStart.value = canStart
    }

    companion object {
        private const val KEY_SELECTED_ID = "selected_id"
    }
}

sealed class FilterEditUiModel(val key: String) {

    class Header(val imageUri: Uri?) : FilterEditUiModel("header")

    class Item(
        val filter: CameraFilter,
        val isSelected: Boolean = false
    ) : FilterEditUiModel("item_${filter.id}") {

        val id: String
            get() = filter.id
    }
}
