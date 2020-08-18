package soup.nolan.work

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import soup.nolan.data.CameraFilterRepository
import soup.nolan.factory.ImageFactory
import soup.nolan.factory.ImageUriFactory
import soup.nolan.filter.stylize.LegacyStyleInput
import soup.nolan.filter.stylize.LegacyStyleTransfer
import timber.log.Timber

class FilterThumbnailWorker @WorkerInject constructor(
    @Assisted @ApplicationContext context: Context,
    @Assisted params: WorkerParameters,
    private val repository: CameraFilterRepository,
    private val imageUriFactory: ImageUriFactory,
    private val imageFactory: ImageFactory,
    private val styleTransfer: LegacyStyleTransfer
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val originalUri = parseOriginalUri()
        Timber.d("doWork: start originalUri=$originalUri")
        val imageSize = styleTransfer.getThumbnailSize()
        val originalBitmap = imageFactory.getBitmap(originalUri, imageSize)

        imageUriFactory.createOriginalImageUri(originalBitmap).let {
            Timber.d("doWork: createOriginalImageUri=$originalUri")
        }

        setProgress(workDataOf(KEY_PROGRESS_LEVEL to 0))
        val list = repository.getAllCameraFilterList()
        list.forEachIndexed { index, filter ->
            Timber.d("doWork: progress=$index/${list.size}, filter=${filter.id}")
            //TODO: convert and save filter image
            val filterBitmap =
                if (filter.input is LegacyStyleInput) {
                    styleTransfer.transform(originalBitmap, filter.input, imageSize)
                } else {
                    originalBitmap
                }
            imageUriFactory.createFilterImageUri(filter, filterBitmap).let {
                Timber.d("doWork: createFilterImageUri(${filter.id})=$it")
            }
            setProgress(workDataOf(KEY_PROGRESS_LEVEL to index + 1))
        }
        Timber.d("doWork: done")
        return Result.success()
    }

    private fun parseOriginalUri(): Uri {
        return inputData.getString(KEY_ORIGINAL_URI)?.toUri()
            ?: imageUriFactory.getDefaultImageUri()
    }

    companion object {

        private const val TAG = "filter_thumbnail"
        private const val KEY_ORIGINAL_URI = "original_uri"
        private const val KEY_PROGRESS_LEVEL = "progress_level"

        fun enqueueWork(context: Context, originalUri: Uri? = null, force: Boolean = false) {
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
