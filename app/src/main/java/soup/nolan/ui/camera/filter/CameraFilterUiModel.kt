package soup.nolan.ui.camera.filter

import soup.nolan.model.CameraFilter

class CameraFilterUiModel(
    val list: List<CameraFilterItemUiModel>
)

data class CameraFilterItemUiModel(
    val filter: CameraFilter
) {
    val id: String
        get() = filter.id
}
