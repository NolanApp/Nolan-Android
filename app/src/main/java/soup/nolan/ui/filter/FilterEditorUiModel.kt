package soup.nolan.ui.filter

import android.net.Uri
import soup.nolan.model.VisualCameraFilter

data class FilterEditorHeaderUiModel(val imageUri: Uri)

data class FilterEditorItemUiModel(
    val filter: VisualCameraFilter,
    val isSelected: Boolean = false
) {
    val key: String = "item_${filter.name}"
}

sealed class FilterEditorUiEvent {
    class TakePicture(val uri: Uri) : FilterEditorUiEvent()
    object PickFromAlbum : FilterEditorUiEvent()
    object GoToCamera : FilterEditorUiEvent()
}
