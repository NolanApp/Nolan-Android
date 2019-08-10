package soup.nolan.ui.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import io.alterac.blurkit.BlurKit
import io.alterac.blurkit.BlurLayout

fun Bitmap.downscale(ratio: Float): Bitmap {
    if (ratio >= 1f) return this
    val scaleRatio = ratio.coerceIn(0f, 1f)
    return Bitmap.createScaledBitmap(
        this,
        (width * scaleRatio).toInt(),
        (height * scaleRatio).toInt(),
        false
    )
}

fun Bitmap.erase(rect: Rect): Bitmap {
    (0 until width).forEach { x ->
        (0 until height).forEach { y ->
            if (rect.contains(x, y).not()) {
                setPixel(x, y, Color.TRANSPARENT)
            }
        }
    }
    return this
}

fun Bitmap.erase(rectList: List<Rect>): Bitmap {
    if (rectList.isNotEmpty()) {
        (0 until width).forEach { x ->
            (0 until height).forEach { y ->
                if (rectList.none { it.contains(x, y) }) {
                    setPixel(x, y, Color.TRANSPARENT)
                }
            }
        }
    }
    return this
}

fun Bitmap.blur(radius: Int = BlurLayout.DEFAULT_BLUR_RADIUS): Bitmap {
    return BlurKit.getInstance().blur(this, radius)
}
