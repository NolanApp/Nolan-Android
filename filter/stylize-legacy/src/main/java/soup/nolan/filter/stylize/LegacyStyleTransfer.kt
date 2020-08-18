package soup.nolan.filter.stylize

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.filter.stylize.internal.Stylize
import soup.nolan.stylize.common.centerCropped

class LegacyStyleTransfer(context: Context) {

    private val stylize: Stylize = Stylize(context.applicationContext)

    suspend fun transform(bitmap: Bitmap, style: LegacyStyleInput, size: Int = IMAGE_SIZE): Bitmap {
        return withContext(Dispatchers.Default) {
            stylize.stylize(bitmap.centerCropped(size), style)
        }
    }

    fun getMaxImageSize(): Int {
        return IMAGE_SIZE
    }

    fun getThumbnailSize(): Int {
        return THUMBNAIL_SIZE
    }

    companion object {
        private const val IMAGE_SIZE = 1024
        private const val THUMBNAIL_SIZE = 384
    }
}
