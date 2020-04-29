package soup.nolan.detect.face.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
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

fun Bitmap.flip(horizontal: Boolean = true): Bitmap {
    val matrix = Matrix().apply {
        if (horizontal) {
            postScale(-1f, 1f, width / 2f, height / 2f)
        } else {
            // vertical
            postScale(1f, -1f, width / 2f, height / 2f)
        }
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
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
