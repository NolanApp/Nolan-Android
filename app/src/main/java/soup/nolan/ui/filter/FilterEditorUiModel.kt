package soup.nolan.ui.filter

import android.net.Uri
import soup.nolan.model.CameraFilter

data class FilterEditorHeaderUiModel(val imageUri: Uri)

data class FilterEditorItemUiModel(
    val filter: CameraFilter,
    val imageUri: Uri?,
    val isSelected: Boolean = false
) {
    val key: String = "item_${filter.id}"
}

sealed class FilterEditorUiEvent {
    class TakePicture(val uri: Uri) : FilterEditorUiEvent()
    object PickFromAlbum : FilterEditorUiEvent()
    object GoToCamera : FilterEditorUiEvent()
}
