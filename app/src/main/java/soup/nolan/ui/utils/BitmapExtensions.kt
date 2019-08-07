package soup.nolan.ui.utils

import android.graphics.Bitmap
import kotlin.math.roundToInt

fun Bitmap.downscale(ratio: Float): Bitmap {
    val scaleRatio = ratio.coerceIn(0f, 1f)
    return Bitmap.createScaledBitmap(
        this,
        (width * scaleRatio).roundToInt(),
        (height * scaleRatio).roundToInt(),
        false
    )
}
