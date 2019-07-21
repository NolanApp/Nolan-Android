package soup.nolan.core.detector.firebase

import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import soup.nolan.core.detector.input.RawImage
import soup.nolan.core.detector.input.RawImageFormat

object FirebaseVisionImage {

    fun from(rawImage: RawImage): FirebaseVisionImage {
        return FirebaseVisionImage.fromByteArray(
            rawImage.data,
            rawImage.metadata()
        )
    }

    private fun RawImage.metadata(): FirebaseVisionImageMetadata {
        return FirebaseVisionImageMetadata.Builder()
            .setHeight(height)
            .setWidth(width)
            .setFormat(format())
            .setRotation(rotation())
            .build()
    }

    private fun RawImage.format(): Int {
        return when (format) {
            RawImageFormat.FORMAT_NV21 -> FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21
            RawImageFormat.FORMAT_YV12 -> FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12
        }
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
