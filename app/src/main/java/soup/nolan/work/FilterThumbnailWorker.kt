package soup.nolan.work

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.data.CameraFilterRepository
import soup.nolan.factory.ImageFactory
import soup.nolan.factory.ImageStore
import soup.nolan.filter.stylize.LegacyStyleInput
import soup.nolan.filter.stylize.LegacyStyleTransfer
import timber.log.Timber

class FilterThumbnailWorker @WorkerInject constructor(
    @Assisted @ApplicationContext context: Context,
    @Assisted params: WorkerParameters,
    private val repository: CameraFilterRepository,
    private val imageStore: ImageStore,
    private val imageFactory: ImageFactory,
    private val styleTransfer: LegacyStyleTransfer
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val originalUri = parseOriginalUri()
        Timber.d("doWork: start originalUri=$originalUri")
        val imageSize = styleTransfer.getThumbnailSize()
        val originalBitmap = imageFactory.getBitmap(originalUri, imageSize)

        imageStore.saveOriginalImageUri(originalBitmap)
        imageStore.clearAllFilterImages()

        val list = repository.getAllFilters()
        list.forEachIndexed { index, filter ->
            Timber.d("doWork: progress=$index/${list.size}, filter=${filter.id}")
            val filterBitmap =
                if (filter.input is LegacyStyleInput) {
                    styleTransfer.transform(originalBitmap, filter.input, imageSize)
                } else {
                    originalBitmap
                }
            imageStore.saveFilterImageUri(filter, filterBitmap).let {
                Timber.d("doWork: createFilterImageUri(${filter.id})=$it")
            }
            setProgress(workDataOf(KEY_PROGRESS_LEVEL to index))
        }
        Timber.d("doWork: done")
        Result.success(workDataOf(KEY_PROGRESS_LEVEL to list.size))
    }

    private fun parseOriginalUri(): Uri {
        return inputData.getString(KEY_ORIGINAL_URI)?.toUri()
            ?: imageStore.getOriginalImageUri()
            ?: imageStore.getDefaultImageUri()
    }

    class DataSource(private val context: Context) {

        fun getLiveData(): LiveData<Unit> {
            return WorkManager.getInstance(context)
                .getWorkInfosForUniqueWorkLiveData(TAG)
                .map { list ->
                    Timber.d("getLiveData: ${list.joinToString { "${it.id} ${it.progress} ${it.outputData}" }}")
                    Unit
                }
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
