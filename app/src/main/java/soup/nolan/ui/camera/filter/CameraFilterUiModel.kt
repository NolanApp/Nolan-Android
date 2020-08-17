package soup.nolan.ui.camera.filter

import android.net.Uri
import soup.nolan.model.CameraFilter

class CameraFilterUiModel(
    val list: List<CameraFilterItemUiModel>
)

data class CameraFilterItemUiModel(
    val filter: CameraFilter,
    val imageUri: Uri
) {
    val id: String
        get() = filter.id
}
