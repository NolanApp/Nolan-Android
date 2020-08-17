package soup.nolan.ui.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.BuildConfig
import soup.nolan.Dependency
import soup.nolan.ui.utils.ImageFactory
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class ShareUriFactory(
    private val context: Context,
    private val imageFactory: ImageFactory = Dependency.imageFactory
) {

    suspend fun createShareImageUri(image: Drawable, withWatermark: Boolean): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                File(context.getShareDirectory(), FILE_NAME)
                    .write(
                        if (withWatermark) {
                            imageFactory.withWatermark(image)
                        } else {
                            imageFactory.getBitmap(image)
                        }
                    )
                    .toContentUri(context)
            } catch (e: FileNotFoundException) {
                Timber.e(e)
                null
            }
        }
    }

    private fun Context.getShareDirectory(): File {
        return File(cacheDir, SHARE_DIR).apply { mkdirs() }
    }

    private fun File.toContentUri(context: Context): Uri {
        return FileProvider.getUriForFile(context, AUTHORITY, this)
    }

    private fun File.write(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100
    ): File {
        bitmap.compress(format, quality, FileOutputStream(this))
        return this
    }

    companion object {

        private const val AUTHORITY = BuildConfig.FILES_AUTHORITY
        private const val SHARE_DIR = "share"
        private const val FILE_NAME = "share_image.jpg"
    }
}
