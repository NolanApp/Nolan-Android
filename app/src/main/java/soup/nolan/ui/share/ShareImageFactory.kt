package soup.nolan.ui.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import soup.nolan.Dependency
import soup.nolan.R
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

object ShareImageFactory {

    private const val AUTHORITY = "soup.nolan.fileprovider"
    private const val SHARE_DIR = "share"
    private const val FILE_NAME = "share_image.jpg"

    fun createShareImageUri(
        context: Context,
        image: Drawable,
        withWatermark: Boolean = Dependency.appSettings.showWatermark
    ): Uri? {
        return try {
            val file = File(context.getShareDirectory(), FILE_NAME)
            image.toBitmap(context, withWatermark)
                .compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
            file.toContentUri(context)
        } catch (e: FileNotFoundException) {
            Timber.e(e)
            null
        }
    }

    private fun Drawable.toBitmap(context: Context, withWatermark: Boolean): Bitmap {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)

        if (withWatermark) {
            ContextCompat.getDrawable(context, R.drawable.share_watermark)?.let {
                val margin = context.resources.getDimensionPixelSize(R.dimen.share_watermark_margin)
                val left = canvas.width - it.intrinsicWidth - margin
                val top = canvas.height - it.intrinsicHeight - margin
                val right = left + it.intrinsicWidth
                val bottom = top + it.intrinsicHeight
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }
        }
        return bitmap
    }

    private fun Context.getShareDirectory(): File {
        return File(cacheDir, SHARE_DIR).apply { mkdirs() }
    }

    private fun File.toContentUri(context: Context): Uri {
        return FileProvider.getUriForFile(context, AUTHORITY, this)
    }
}