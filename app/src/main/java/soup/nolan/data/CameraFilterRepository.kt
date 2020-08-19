package soup.nolan.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import soup.nolan.model.CameraFilter
import soup.nolan.model.CameraFilter.*
import soup.nolan.work.FilterThumbnailWorker

interface CameraFilterRepository {

    fun getAllCameraFilterList(): List<CameraFilter>

    fun updateFilterImages(originalUri: Uri)

    fun getAllFilterLiveData(): LiveData<List<CameraFilter>>
}

class CameraFilterRepositoryImpl(
    private val context: Context,
    private val dataSource: FilterThumbnailWorker.DataSource
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

    override fun updateFilterImages(originalUri: Uri) {
        FilterThumbnailWorker.execute(context, originalUri, force = true)
    }

    override fun getAllFilterLiveData(): LiveData<List<CameraFilter>> {
        return dataSource.getLiveData().map { list }
    }
}
