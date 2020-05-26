package soup.nolan.stylize.common

import android.graphics.*

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

fun Bitmap.grayscale(): Bitmap {
    val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val matrix = ColorMatrix().apply { setSaturation(0f) }
    val filter = ColorMatrixColorFilter(matrix)
    val paint = Paint().apply { colorFilter = filter }
    Canvas(grayscale).drawBitmap(this, 0f, 0f, paint)
    return grayscale
}
