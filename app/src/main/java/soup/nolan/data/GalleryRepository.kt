package soup.nolan.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.model.Media
import timber.log.Timber
import java.io.File

interface GalleryRepository {
    suspend fun getMediaList(): List<Media>
}

class GalleryRepositoryImpl(private val context: Context) : GalleryRepository {

    override suspend fun getMediaList(): List<Media> {
        return withContext(Dispatchers.IO) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val sortOrder = "$INDEX_DATE_ADDED DESC"
            val projection = arrayOf(
                INDEX_MEDIA_ID,
                INDEX_MEDIA_URI,
                INDEX_DATE_ADDED
            )
            val selection = MediaStore.Images.Media.SIZE + " > 0"
            context.contentResolver
                .query(uri, projection, selection, null, sortOrder)
                ?.use { cursor ->
                    generateSequence { if (cursor.moveToNext()) cursor else null }
                        .mapNotNull { it.getImage() }
                        .toList()
                }
                .orEmpty()
        }
    }

    private fun Cursor.getImage(): Media? {
        return try {
            Media(getMediaUri(), getLong(getColumnIndex(INDEX_DATE_ADDED)))
        } catch (e: Exception) {
            Timber.w(e)
            null
        }
    }

    private fun Cursor.getMediaUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val id = getLong(getColumnIndex(INDEX_MEDIA_ID))
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ContentUris.withAppendedId(contentUri, id)
        } else {
            val mediaPath = getString(getColumnIndex(INDEX_MEDIA_URI))
            Uri.fromFile(File(mediaPath))
        }
    }

    companion object {

        private const val INDEX_MEDIA_ID = MediaStore.MediaColumns._ID
        private const val INDEX_MEDIA_URI = MediaStore.MediaColumns.DATA
        private const val INDEX_DATE_ADDED = MediaStore.MediaColumns.DATE_ADDED
    }
}