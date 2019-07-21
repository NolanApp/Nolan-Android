package soup.nolan.core.detector

import android.util.Size
import soup.nolan.model.Face

interface FaceDetector : Detector {

    fun setCallback(callback: Callback?)

    interface Callback {

        fun onIdle()

        fun onDetected(frame: Size, faceList: List<Face>)

        fun onDetectFailed()
    }
}
