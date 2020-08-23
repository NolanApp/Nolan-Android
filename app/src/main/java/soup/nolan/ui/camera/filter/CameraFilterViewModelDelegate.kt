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
import soup.nolan.ui.utils.setValueIfNew
import soup.nolan.work.FilterThumbnailWorker

interface CameraFilterViewModelDelegate {

    val originalImageUri: LiveData<Uri>
    val selectedFilter: LiveData<CameraFilter>
    val selectedVisualFilter: LiveData<VisualCameraFilter>
    val selectedPosition: LiveData<Int>
    val allVisualFiltersLiveData: LiveData<List<VisualCameraFilter>>

    fun generateFilterThumbnailsIfNeeded()
    fun onOriginImageChanged(imageUri: Uri)
    fun onFilterSelected(visualFilter: VisualCameraFilter)
    fun getSelectedFilter(): CameraFilter?
}

class CameraFilterViewModelDelegateImpl(
    private val context: Context,
    private val repository: CameraFilterRepository,
    private val imageStore: ImageStore,
    private val appSettings: AppSettings
) : CameraFilterViewModelDelegate {

    private val dataSource = FilterThumbnailWorker.DataSource(context)

    private val _originalImageUri = MutableLiveData<Uri>(
        imageStore.getOriginalImageUri()
            ?: imageStore.getDefaultImageUri()
    )
    override val originalImageUri: LiveData<Uri>
        get() = _originalImageUri

    private val _selectedFilter = MutableLiveData<CameraFilter>(
        repository.getCameraFilter(filterId = appSettings.lastFilterId ?: CameraFilter.default.id)
    )
    override val selectedFilter
        get() = _selectedFilter

    override val selectedVisualFilter: LiveData<VisualCameraFilter> =
        selectedFilter.map { selectedFilter ->
            VisualCameraFilter(
                selectedFilter,
                imageUri = imageStore.getFilterImageUri(selectedFilter),
                inProgress = false
            )
        }

    override val selectedPosition =
        selectedFilter.map { selectedFilter ->
            repository.getAllFilters().indexOfFirst { it.id == selectedFilter.id }
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

    override fun generateFilterThumbnailsIfNeeded() {
        if (appSettings.filterThumbnailsGenerated.not()) {
            FilterThumbnailWorker.execute(context)
        }
    }

    override fun onOriginImageChanged(imageUri: Uri) {
        if (_originalImageUri.value != imageUri) {
            _originalImageUri.value = imageUri
            FilterThumbnailWorker.execute(context, imageUri, force = true)
        }
    }

    override fun onFilterSelected(visualFilter: VisualCameraFilter) {
        _selectedFilter.setValueIfNew(visualFilter.filter)
        appSettings.lastFilterId = visualFilter.id
    }

    override fun getSelectedFilter(): CameraFilter? {
        return _selectedFilter.value
    }
}
