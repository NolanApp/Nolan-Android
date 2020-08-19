package soup.nolan.model

import android.net.Uri

class VisualCameraFilter(
    val filter: CameraFilter,
    val imageUri: Uri?
) {
    val name: String
        get() = filter.id
}
