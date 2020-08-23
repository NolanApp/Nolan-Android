package soup.nolan.ui.camera.filter

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import soup.nolan.data.CameraFilterRepository
import soup.nolan.factory.ImageStore
import soup.nolan.model.CameraFilter
import soup.nolan.model.VisualCameraFilter
import soup.nolan.settings.AppSettings
import soup.nolan.work.FilterThumbnailWorker
import timber.log.Timber

interface CameraFilterViewModelDelegate {

    val originalImageUri: LiveData<Uri>

    val selectedFilter: LiveData<CameraFilter>

    val selectedVisualFilter: LiveData<VisualCameraFilter>

    val allVisualFiltersLiveData: LiveData<List<VisualCameraFilter>>

    val selectedPosition: LiveData<Int>

    fun generateFilterThumbnailsIfNeeded()

    fun onOriginImageChanged(imageUri: Uri)

    fun onFilterSelect(item: VisualCameraFilter)

    fun getSelectedCameraFilter(): CameraFilter
}

class CameraFilterViewModelDelegateImpl(
    private val context: Context,
    private val repository: CameraFilterRepository,
    private val dataSource: FilterThumbnailWorker.DataSource,
    private val imageStore: ImageStore,
    private val appSettings: AppSettings
) : CameraFilterViewModelDelegate {

    private val _originalImageUri = MutableLiveData<Uri>(
        imageStore.getOriginalImageUri()
            ?: imageStore.getDefaultImageUri()
    )
    override val originalImageUri: LiveData<Uri>
        get() = _originalImageUri

    private val _selectedFilterId = MutableLiveData<String>(appSettings.lastFilterId ?: CameraFilter.default.id)

    override val selectedFilter: LiveData<CameraFilter> =
        _selectedFilterId.map { selectedFilterId ->
            repository.getCameraFilter(filterId = selectedFilterId)
        }

    override val selectedVisualFilter: LiveData<VisualCameraFilter> =
        selectedFilter.map { selectedFilter ->
            VisualCameraFilter(
                selectedFilter,
                imageUri = imageStore.getFilterImageUri(selectedFilter),
                inProgress = false
            )
        }

    override val selectedPosition =
        _selectedFilterId.map { selectedFilterId ->
            repository.getAllFilters().indexOfFirst { it.id == selectedFilterId }
        }

    override val allVisualFiltersLiveData: LiveData<List<VisualCameraFilter>>
        get() = dataSource.getStatusLiveData().map { status ->
            repository.getAllFilters().mapIndexed { index, cameraFilter ->
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

    override fun getSelectedCameraFilter(): CameraFilter {
        return repository.getAllFilters()
            .firstOrNull { it.id == appSettings.lastFilterId }
            ?: CameraFilter.default
    }

    override fun generateFilterThumbnailsIfNeeded() {
        if (appSettings.filterThumbnailsGenerated.not()) {
            FilterThumbnailWorker.execute(context)
        }
    }

    override fun onOriginImageChanged(imageUri: Uri) {
        _originalImageUri.value = imageUri
        FilterThumbnailWorker.execute(context, imageUri, force = true)
    }

    override fun onFilterSelect(item: VisualCameraFilter) {
        if (appSettings.lastFilterId == item.id) {
            Timber.w("onFilterSelect: ${item.id} is already selected!")
            return
        }
        appSettings.lastFilterId = item.id
        _selectedFilterId.value = item.id
    }
}
