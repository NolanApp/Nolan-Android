package soup.nolan.ui.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Size
import androidx.core.graphics.rotationMatrix
import androidx.core.graphics.scaleMatrix
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.math.max

class ImageFactory(private val context: Context) {

    suspend fun getBitmap(fileUri: Uri): Bitmap {
        return withContext(Dispatchers.IO) {
            val sampleMaxSize = 1024.toDouble()
            context.contentResolver.toSamplingImage(fileUri, sampleMaxSize)
                ?: throw IllegalStateException("Can't decode bitmap from Uri($fileUri)")
        }
    }

    private fun ContentResolver.toSamplingImage(uri: Uri, sampleMaxSize: Double): Bitmap? {
        val originalSize = originalSizeOf(uri)
        if (originalSize.width <= 0 || originalSize.height <= 0) {
            return null
        }
        return openInputStream(uri).use {
            val originalMaxSize = max(originalSize.width, originalSize.height)
            val ratio = max(1.0, originalMaxSize / sampleMaxSize)
            val options = BitmapFactory.Options().apply { inSampleSize = sampleSizeOf(ratio) }
            BitmapFactory
                .decodeStream(it, null, options)
                ?.applyOrientation(exifInterfaceOf(uri).extractOrientation())
        }
    }

    private fun ContentResolver.originalSizeOf(uri: Uri): Size {
        return openInputStream(uri)?.use {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(it, null, options)
            options.inJustDecodeBounds = false
            Size(options.outWidth, options.outHeight)
        } ?: Size(-1, -1)
    }

    private fun sampleSizeOf(ratio: Double): Int {
        val k = Integer.highestOneBit(floor(ratio).toInt())
        return if (k == 0) 1 else k
    }

    private fun ContentResolver.exifInterfaceOf(uri: Uri): ExifInterface? {
        return openInputStream(uri)?.use { ExifInterface(it) }
    }

    private fun ExifInterface?.extractOrientation(defaultValue: Int = ExifInterface.ORIENTATION_NORMAL): Int {
        return this?.getAttributeInt(ExifInterface.TAG_ORIENTATION, defaultValue) ?: defaultValue
    }

    private fun Bitmap.applyOrientation(orientation: Int): Bitmap {
        val matrix: Matrix? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotationMatrix(90.0f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotationMatrix(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotationMatrix(-90.0f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> scaleMatrix(-1.0f, 1.0f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> scaleMatrix(1.0f, -1.0f)
            ExifInterface.ORIENTATION_TRANSPOSE -> Matrix().apply {
                postRotate(90.0f)
                postScale(-1.0f, 1.0f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> Matrix().apply {
                postRotate(-90.0f)
                postScale(-1.0f, 1.0f)
            }
            else -> null
        }
        val new = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (new != this) {
            recycle()
        }
        return new
    }
}
