package soup.nolan.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import soup.nolan.core.detector.FaceDetector
import soup.nolan.core.detector.model.RawImage

class FaceImageAnalyzer(
    private val detector: FaceDetector
) : ImageAnalysis.Analyzer {

    var isMirror: Boolean = false

    override fun analyze(proxy: ImageProxy, rotationDegrees: Int) {
        val image = proxy.image ?: return
        if (detector.isInDetecting().not()) {
            detector.detect(
                RawImage(
                    image,
                    proxy.width,
                    proxy.height,
                    rotationDegrees,
                    isMirror
                )
            )
        }
    }
}
