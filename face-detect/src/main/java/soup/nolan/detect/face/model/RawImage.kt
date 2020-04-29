package soup.nolan.detect.face.model

import android.media.Image

class RawImage(
    val image: Image,
    val width: Int,
    val height: Int,
    val rotationDegrees: Int,
    val isMirror: Boolean
)
