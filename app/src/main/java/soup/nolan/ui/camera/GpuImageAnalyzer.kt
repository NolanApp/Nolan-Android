package soup.nolan.ui.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class GpuImageAnalyzer(
    private val consumer: (ByteArray, width: Int, height: Int) -> Unit
) : ImageAnalysis.Analyzer {

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

    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        consumer(image.data, image.width, image.height)
    }
}
