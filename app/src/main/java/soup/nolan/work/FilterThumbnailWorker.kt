package soup.nolan.work

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import soup.nolan.factory.ImageUriFactory
import soup.nolan.model.CameraFilter
import timber.log.Timber

class FilterThumbnailWorker @WorkerInject constructor(
    @Assisted @ApplicationContext context: Context,
    @Assisted params: WorkerParameters,
    private val imageUriFactory: ImageUriFactory
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val originalUri = parseOriginalUri()
        Timber.d("doWork: originalUri=$originalUri")

        val list = CameraFilter.all()
        list.forEachIndexed { index, filter ->
            Timber.d("doWork: progress=$index/${list.size}, filter=${filter.id}")
            //TODO: convert and save filter image
            setProgress(workDataOf(KEY_PROGRESS_LEVEL to index))
        }
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
