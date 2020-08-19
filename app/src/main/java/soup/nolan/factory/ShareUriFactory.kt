package soup.nolan.factory

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.utils.toContentUri
import soup.nolan.utils.write
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException

interface ShareUriFactory {
    suspend fun createShareImageUri(image: Drawable, withWatermark: Boolean): Uri?
}

class ShareUriFactoryImpl(
    private val context: Context,
    private val imageFactory: ImageFactory
) : ShareUriFactory {

    override suspend fun createShareImageUri(image: Drawable, withWatermark: Boolean): Uri? {
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

    companion object {

        private const val SHARE_DIR = "share"
        private const val FILE_NAME = "share_image.jpg"
    }
}
