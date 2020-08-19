package soup.nolan.model

import android.net.Uri

class VisualCameraFilter(
    val filter: CameraFilter,
    val imageUri: Uri?
) {
    val id: String
        get() = filter.id
}
