package soup.nolan.core.detector

import soup.nolan.core.detector.model.Frame
import soup.nolan.model.Face

interface FaceDetector : Detector {

    fun setCallback(callback: Callback?)

    interface Callback {

        fun onDetecting(frame: Frame)

        fun onDetected(faceList: List<Face>)

        fun onDetectFailed()
    }
}
