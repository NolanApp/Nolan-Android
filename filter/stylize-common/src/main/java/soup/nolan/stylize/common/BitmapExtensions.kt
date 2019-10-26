package soup.nolan.stylize.common

import android.graphics.Bitmap
import android.graphics.Canvas

fun Bitmap.centerCropped(size: Int): Bitmap {
    return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also {
        val frameToCropTransform = ImageUtils.getTransformationMatrix(
            width, height,
            size, size,
            0, true
        )
        Canvas(it).drawBitmap(this, frameToCropTransform, null)
    }
}

fun Bitmap.toPixels(intValues: IntArray): IntArray {
    getPixels(intValues, 0, width, 0, 0, width, height)
    return intValues
}
