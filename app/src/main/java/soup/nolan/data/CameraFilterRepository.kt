package soup.nolan.data

import android.content.Context
import android.net.Uri
import soup.nolan.work.FilterThumbnailWorker

interface CameraFilterRepository {

    fun fetchOriginalUri(uri: Uri)
}

class CameraFilterRepositoryImpl(
    private val context: Context
) : CameraFilterRepository {

    override fun fetchOriginalUri(uri: Uri) {
        //TODO: reset thumbnail list
        FilterThumbnailWorker.enqueueWork(context, uri, force = true)
    }
}
