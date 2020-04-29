package soup.nolan.detect.face

import android.graphics.Bitmap
import soup.nolan.detect.face.model.Face
import soup.nolan.detect.face.model.Frame

interface FaceDetector : Detector {

    fun setCallback(callback: Callback?)

    interface Callback {

        fun onDetecting(frame: Frame)

        fun onDetected(originalImage: Bitmap, faceList: List<Face>)

        fun onDetectFailed()
    }
}
