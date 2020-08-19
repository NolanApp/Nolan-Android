package soup.nolan.factory

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.R
import soup.nolan.model.CameraFilter
import soup.nolan.utils.toContentUri
import soup.nolan.utils.write
import java.io.File

interface ImageStore {

    /**
     * 카메라 촬영에 넘겨줄 이미지 [Uri]를 반환한다.
     */
    fun createCameraImageUri(): Uri

    /**
     * 필터 생성에 사용할 기본 이미지 [Uri]를 반환한다.
     */
    fun getDefaultImageUri(): Uri

    /**
     * 필터 생성에 사용한 원본 이미지 [Uri]를 반환한다.
     */
    fun getOriginalImageUri(): Uri?

    /**
     * 필터에 맞는 썸네일 이미지 [Uri]를 반환한다.
     */
    fun getFilterImageUri(filter: CameraFilter): Uri?

    /**
     * 필터 생성에 사용할 원본 이미지 [Uri]를 생성한다.
     */
    suspend fun saveOriginalImageUri(bitmap: Bitmap): Uri

    /**
     * 필터 이미지를 저장한 후, 이미지 [Uri]를 생성한다.
     */
    suspend fun saveFilterImageUri(filter: CameraFilter, bitmap: Bitmap): Uri

    /**
     * 필터 썸네일 이미지를 모두 삭제한다.
     */
    suspend fun clearAllFilterImages()
}

class ImageStoreImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ImageStore {

    override fun createCameraImageUri(): Uri {
        return File(context.getDirectory(IMAGE_DIR), FILE_NAME_CAMERA)
            .toContentUri(context)
    }

    override fun getDefaultImageUri(): Uri {
        val resId = R.drawable.default_image
        return context.resources.run {
            Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getResourcePackageName(resId))
                .appendPath(getResourceTypeName(resId))
                .appendPath(getResourceEntryName(resId))
                .build()
        }
    }

    override fun getOriginalImageUri(): Uri? {
        return File(context.getDirectory(IMAGE_DIR), FILE_NAME_ORIGIN)
            .takeIf { it.exists() }
            ?.toUri()
    }

    override fun getFilterImageUri(filter: CameraFilter): Uri? {
        return File(context.getDirectory(IMAGE_FILTER_DIR), filter.fileName())
            .takeIf { it.exists() }
            ?.toUri()
    }

    override suspend fun saveOriginalImageUri(bitmap: Bitmap): Uri {
        return withContext(ioDispatcher) {
            File(context.getDirectory(IMAGE_DIR), FILE_NAME_ORIGIN)
                .write(bitmap)
                .toUri()
        }
    }

    override suspend fun saveFilterImageUri(filter: CameraFilter, bitmap: Bitmap): Uri {
        return withContext(ioDispatcher) {
            File(context.getDirectory(IMAGE_FILTER_DIR), filter.fileName())
                .write(bitmap)
                .toUri()
        }
    }

    override suspend fun clearAllFilterImages() {
        withContext(ioDispatcher) {
            context.getDirectory(IMAGE_DIR).deleteRecursively()
        }
    }

    private fun Context.getDirectory(path: String): File {
        return File(filesDir, path).apply { mkdirs() }
    }

    companion object {
        private const val IMAGE_DIR = "image_manager/"
        private const val IMAGE_FILTER_DIR = "image_manager/filter/"
        private const val FILE_NAME_CAMERA = "camera_image.jpg"
        private const val FILE_NAME_ORIGIN = "origin_image.jpg"

        private fun CameraFilter.fileName(): String {
            return "filter_$id.jpg"
        }
    }
}
