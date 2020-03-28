package soup.nolan.ui.camera

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import soup.nolan.core.detector.firebase.VisionImage
import soup.nolan.core.detector.model.RawImage
import soup.nolan.ui.utils.flip

class GpuImageAnalyzer(
    private val consumer: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    var isMirror: Boolean = false

    override fun analyze(image: ImageProxy) {
        image.use { proxy ->
            proxy.image?.use {
                val rawImage = RawImage(
                    it,
                    proxy.width,
                    proxy.height,
                    proxy.imageInfo.rotationDegrees,
                    isMirror
                )
                consumer(rawImage.toBitmap())
            }
        }
    }

    private fun RawImage.toBitmap(): Bitmap {
        return VisionImage.from(this).bitmap
            .run {
                if (isMirror) {
                    flip()
                } else {
                    this
                }
            }
    }
}
