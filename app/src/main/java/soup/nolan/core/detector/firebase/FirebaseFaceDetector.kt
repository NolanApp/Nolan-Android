package soup.nolan.core.detector.firebase

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import soup.nolan.core.detector.FaceDetector
import soup.nolan.core.detector.model.Frame
import soup.nolan.core.detector.model.RawImage
import soup.nolan.model.Face
import java.util.concurrent.atomic.AtomicBoolean

class FirebaseFaceDetector : FaceDetector {

    private val coreDetector: FirebaseVisionFaceDetector

    private val isInDetecting = AtomicBoolean(false)

    private var callback: FaceDetector.Callback? = null

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .build()
        coreDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
    }

    override fun isInDetecting(): Boolean {
        return isInDetecting.get()
    }

    override fun detect(image: RawImage) {
        if (isInDetecting.compareAndSet(false, true)) {
            doOnDetectComplete(image) {
                isInDetecting.set(false)
            }
        }
    }

    override fun setCallback(callback: FaceDetector.Callback?) {
        this.callback = callback
    }

    private inline fun doOnDetectComplete(
        rawImage: RawImage,
        crossinline completeAction: () -> Unit
    ) {
        callback?.onDetecting(Frame(rawImage.width, rawImage.height))
        coreDetector.detectInImage(FirebaseVisionImage.from(rawImage))
            .addOnSuccessListener { faceList ->
                callback?.onDetected(
                    faceList.mapNotNull { Face(it.boundingBox) })
            }
            .addOnFailureListener {
                callback?.onDetectFailed()
            }
            .addOnCompleteListener {
                completeAction()
            }
    }
}
