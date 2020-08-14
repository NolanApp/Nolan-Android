package soup.nolan.filter.stylize

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.filter.stylize.internal.Stylize
import soup.nolan.stylize.common.centerCropped

class LegacyStyleTransfer(context: Context) {

    private val stylize: Stylize =
        Stylize(context.applicationContext)

    suspend fun transform(bitmap: Bitmap, style: LegacyStyleInput): Bitmap {
        return withContext(Dispatchers.Default) {
            stylize.stylize(bitmap.centerCropped(IMAGE_SIZE), style)
        }
    }

    companion object {
        const val IMAGE_SIZE = 1024
    }
}
