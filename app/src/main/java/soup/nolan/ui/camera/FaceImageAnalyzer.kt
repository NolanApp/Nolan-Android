package soup.nolan.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import soup.nolan.core.detector.FaceDetector
import soup.nolan.core.detector.model.RawImage

class FaceImageAnalyzer(
    private val detector: FaceDetector
) : ImageAnalysis.Analyzer {

    var isMirror: Boolean = false

    override fun analyze(image: ImageProxy) {
        image.use { proxy ->
            image.image?.use {
                if (detector.isInDetecting().not()) {
                    detector.detect(
                        RawImage(
                            it,
                            proxy.width,
                            proxy.height,
                            proxy.imageInfo.rotationDegrees,
                            isMirror
                        )
                    )
                }
            }
        }
    }
}
