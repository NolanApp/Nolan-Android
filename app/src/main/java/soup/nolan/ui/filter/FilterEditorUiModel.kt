package soup.nolan.ui.filter

import android.net.Uri
import soup.nolan.model.CameraFilter

sealed class FilterEditorUiModel(val key: String) {

    class Header(
        val imageUri: Uri?
    ) : FilterEditorUiModel("header")

    class Item(
        val filter: CameraFilter,
        val isSelected: Boolean = false
    ) : FilterEditorUiModel("item_${filter.id}")
}

sealed class FilterEditorUiEvent {
    object CameraPicker : FilterEditorUiEvent()
    object AlbumPicker : FilterEditorUiEvent()
}
