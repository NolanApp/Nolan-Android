package soup.nolan.filter.stylize

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.stylize.common.ImageUtils

class LegacyStyleTransfer(context: Context) {

    private val stylize: Stylize = Stylize(context.applicationContext)

    suspend fun transform(bitmap: Bitmap, style: LegacyStyleInput): Bitmap {
        return withContext(Dispatchers.Default) {
            getStylizedImageFrom(bitmap, style)
        }
    }

    private fun getStylizedImageFrom(bitmap: Bitmap, style: LegacyStyleInput, desiredSize: Int = 1024): Bitmap {
        val previewWidth = bitmap.width
        val previewHeight = bitmap.height
        val frameToCropTransform = ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            desiredSize, desiredSize,
            0, true
        )
        val croppedBitmap = Bitmap.createBitmap(desiredSize, desiredSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(croppedBitmap)
        canvas.drawBitmap(bitmap, frameToCropTransform, null)

        return stylize.stylize(croppedBitmap, style)
    }
}
