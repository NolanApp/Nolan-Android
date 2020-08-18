package soup.nolan.data

import android.content.Context
import android.net.Uri
import soup.nolan.model.CameraFilter
import soup.nolan.model.CameraFilter.*
import soup.nolan.work.FilterThumbnailWorker

interface CameraFilterRepository {

    fun getAllCameraFilterList(): List<CameraFilter>

    fun fetchOriginalUri(uri: Uri)
}

class CameraFilterRepositoryImpl(
    private val context: Context
) : CameraFilterRepository {

    private val list = listOf(
        OR,
        A01, A02, A03, A04, A05, A06, A07, A08, A09, A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
        A21, A22, A23, A24, A25, A26
    )

    override fun getAllCameraFilterList(): List<CameraFilter> {
        return list
    }

    override fun fetchOriginalUri(uri: Uri) {
        //TODO: reset thumbnail list
        FilterThumbnailWorker.enqueueWork(context, uri, force = true)
    }
}
