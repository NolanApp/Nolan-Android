package soup.nolan.detect.face.internal

import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import soup.nolan.detect.face.model.RawImage

object VisionImage {

    fun from(rawImage: RawImage): FirebaseVisionImage {
        return FirebaseVisionImage.fromMediaImage(
            rawImage.image,
            rawImage.rotation()
        )
    }

    private fun RawImage.rotation(): Int {
        return when (rotationDegrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> FirebaseVisionImageMetadata.ROTATION_0
        }
    }
}
