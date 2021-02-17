package soup.nolan.work

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.data.CameraFilterRepository
import soup.nolan.factory.ImageFactory
import soup.nolan.factory.ImageStore
import soup.nolan.filter.stylize.LegacyStyleInput
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.settings.AppSettings
import timber.log.Timber

@HiltWorker
class FilterThumbnailWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: CameraFilterRepository,
    private val imageStore: ImageStore,
    private val imageFactory: ImageFactory,
    private val styleTransfer: LegacyStyleTransfer,
    private val appSettings: AppSettings
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val originalUri = parseOriginalUri()
        val imageSize = styleTransfer.getThumbnailSize()
        val originalBitmap = imageFactory.getBitmap(originalUri, imageSize)

        imageStore.saveOriginalImageUri(originalBitmap)
        imageStore.clearAllFilterImages()
        setProgress(workDataOf(KEY_PROGRESS_LEVEL to 0))

        repository.getAllFilters().forEachIndexed { index, filter ->
            Timber.d("doWork: progress=$index, filter=${filter.id}")
            val filterBitmap =
                if (filter.input is LegacyStyleInput) {
                    styleTransfer.transform(originalBitmap, filter.input, imageSize)
                } else {
                    originalBitmap
                }
            imageStore.saveFilterImageUri(filter, filterBitmap)
            setProgress(workDataOf(KEY_PROGRESS_LEVEL to index + 1))
        }
        Timber.d("doWork: done")
        appSettings.filterThumbnailsGenerated = true
        Result.success()
    }

    private fun parseOriginalUri(): Uri {
        return inputData.getString(KEY_ORIGINAL_URI)?.toUri()
            ?: imageStore.getDefaultImageUri()
    }

    class DataSource(private val context: Context) {

        fun getStatusLiveData(): LiveData<Status> {
            return WorkManager.getInstance(context)
                .getWorkInfosForUniqueWorkLiveData(TAG)
                .map { it.firstOrNull()?.toStatus() ?: Status() }
        }

        private fun WorkInfo.toStatus(): Status {
            return Status(
                complete = state == WorkInfo.State.SUCCEEDED,
                progress = if (state == WorkInfo.State.RUNNING) {
                    progress.getInt(KEY_PROGRESS_LEVEL, Status.UNKNOWN_PROGRESS)
                } else {
                    Status.UNKNOWN_PROGRESS
                }
            )
        }
    }

    data class Status(
        val complete: Boolean = false,
        val progress: Int = UNKNOWN_PROGRESS
    ) {
        companion object {
            const val UNKNOWN_PROGRESS = -1
        }
    }

    companion object {
        private const val TAG = "filter_thumbnail"
        private const val KEY_ORIGINAL_URI = "original_uri"
        private const val KEY_PROGRESS_LEVEL = "progress_level"

        fun execute(context: Context, originalUri: Uri? = null, force: Boolean = false) {
            val request = OneTimeWorkRequestBuilder<FilterThumbnailWorker>()
                .setInputData(workDataOf(KEY_ORIGINAL_URI to originalUri?.toString()))
                .build()
            WorkManager.getInstance(context).apply {
                if (force) {
                    cancelUniqueWork(TAG)
                    enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
                } else {
                    enqueueUniqueWork(TAG, ExistingWorkPolicy.KEEP, request)
                }
            }
        }
    }
}
