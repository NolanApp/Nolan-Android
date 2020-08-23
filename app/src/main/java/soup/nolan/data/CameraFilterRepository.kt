package soup.nolan.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import soup.nolan.factory.ImageStore
import soup.nolan.model.CameraFilter
import soup.nolan.model.CameraFilter.*
import soup.nolan.model.VisualCameraFilter
import soup.nolan.work.FilterThumbnailWorker

interface CameraFilterRepository {

    fun getAllFilters(): List<CameraFilter>

    fun getAllVisualFiltersLiveData(): LiveData<List<VisualCameraFilter>>

    fun updateFilterImages(originalUri: Uri)
}

class CameraFilterRepositoryImpl(
    private val context: Context,
    private val dataSource: FilterThumbnailWorker.DataSource,
    private val imageStore: ImageStore
) : CameraFilterRepository {

    private val list = listOf(
        OR,
        A01, A02, A03, A04, A05, A06, A07, A08, A09, A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
        A21, A22, A23, A24, A25, A26
    )

    override fun getAllFilters(): List<CameraFilter> {
        return list
    }

    override fun getAllVisualFiltersLiveData(): LiveData<List<VisualCameraFilter>> {
        return dataSource.getStatusLiveData().map { status ->
            list.mapIndexed { index, cameraFilter ->
                val imageUri = if (status.complete || index < status.progress) {
                    imageStore.getFilterImageUri(cameraFilter)
                } else {
                    null
                }
                VisualCameraFilter(
                    cameraFilter,
                    imageUri,
                    inProgress = index == status.progress
                )
            }
        }
    }

    override fun updateFilterImages(originalUri: Uri) {
        FilterThumbnailWorker.execute(context, originalUri, force = true)
    }
}
