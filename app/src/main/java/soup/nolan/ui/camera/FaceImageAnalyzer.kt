package soup.nolan.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import soup.nolan.core.detector.FaceDetector
import soup.nolan.core.detector.input.RawImage
import soup.nolan.core.detector.input.RawImageFormat

class FaceImageAnalyzer(
    private val detector: FaceDetector
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        if (detector.isInDetecting().not()) {
            detector.detect(rawImage(image, rotationDegrees))
        }
    }

    private fun rawImage(image: ImageProxy, rotationDegrees: Int): RawImage =
        RawImage(
            format = RawImageFormat.FORMAT_YV12,
            data = image.data,
            width = image.width,
            height = image.height,
            rotationDegrees = rotationDegrees
        )

    private val ImageProxy.data: ByteArray
        get() {
            val y = planes[0]
            val u = planes[1]
            val v = planes[2]
            val Yb = y.buffer.remaining()
            val Ub = u.buffer.remaining()
            val Vb = v.buffer.remaining()
            return ByteArray(Yb + Ub + Vb).apply {
                y.buffer.get(this, 0, Yb)
                u.buffer.get(this, Yb, Ub)
                v.buffer.get(this, Yb + Ub, Vb)
            }
        }
}
