package soup.nolan.model

import android.net.Uri

data class VisualCameraFilter(
    val filter: CameraFilter,
    val imageUri: Uri?,
    val inProgress: Boolean
) {
    val id: String
        get() = filter.id
}
