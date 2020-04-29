package soup.nolan.detect.face

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import soup.nolan.detect.face.model.RawImage

/**
 * private val faceImageAnalyzer by lazyFast {
 *     val detector: FaceDetector = FirebaseFaceDetector()
 *     .apply {
 *         setCallback(object : FaceDetector.Callback {
 *
 *             override fun onDetecting(frame: Frame) {
 *                 val min = min(frame.width, frame.height)
 *                 val max = max(frame.width, frame.height)
 *                 binding.faceBlurView.setCameraInfo(min, max)
 *                 binding.faceBlurView.clear()
 *             }
 *
 *             override fun onDetected(originalImage: Bitmap, faceList: List<Face>) {
 *                 binding.faceBlurView.renderFaceList(originalImage, faceList)
 *             }
 *
 *             override fun onDetectFailed() {
 *                 binding.faceBlurView.run {
 *                     clear()
 *                     postInvalidate()
 *                 }
 *             }
 *         })
 *     }
 *     FaceImageAnalyzer(detector).apply {
 *         isMirror = binding.cameraPreview.cameraLensFacing == LENS_FACING_FRONT
 *     }
 * }
 */
class FaceImageAnalyzer(
    private val detector: FaceDetector
) : ImageAnalysis.Analyzer {

    var isMirror: Boolean = false

    override fun analyze(image: ImageProxy) {
        image.use { proxy ->
            proxy.image?.use {
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
