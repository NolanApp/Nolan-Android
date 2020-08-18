package soup.nolan.factory

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import soup.nolan.R
import soup.nolan.model.CameraFilter
import soup.nolan.utils.toContentUri
import soup.nolan.utils.write
import java.io.File

interface ImageUriFactory {

    /**
     * 카메라로 촬영할 이미지 [Uri]를 반환한다.
     */
    fun createCameraImageUri(): Uri

    /**
     * 필터 생성에 사용할 기본 이미지 [Uri]를 반환한다.
     */
    fun getDefaultImageUri(): Uri

    /**
     * 필터에 맞는 썸네일 이미지 [Uri]를 반환한다.
     */
    fun getFilterImageUri(filter: CameraFilter): Uri?

    /**
     * 필터 생성에 사용할 원본 이미지 [Uri]를 생성한다.
     */
    fun createOriginalImageUri(bitmap: Bitmap): Uri

    /**
     * 필터 이미지를 저장한 후, 이미지 [Uri]를 생성한다.
     */
    fun createFilterImageUri(filter: CameraFilter, bitmap: Bitmap): Uri
}

class ImageUriFactoryImpl(
    private val context: Context
) : ImageUriFactory {

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

    override fun getFilterImageUri(filter: CameraFilter): Uri? {
        val saveDir = context.getDirectory(FILTER_DIR)
        return File(saveDir, filter.fileName())
            .takeIf { it.exists() }?.toUri()
    }

    override fun createOriginalImageUri(bitmap: Bitmap): Uri {
        return File(context.getDirectory(IMAGE_DIR), FILE_NAME_ORIGIN)
            .write(bitmap)
            .toUri()
    }

    override fun createFilterImageUri(filter: CameraFilter, bitmap: Bitmap): Uri {
        return File(context.getDirectory(FILTER_DIR), filter.fileName())
                .write(bitmap)
                .toUri()
    }

    private fun Context.getDirectory(path: String): File {
        return File(filesDir, path).apply { mkdirs() }
    }

    companion object {
        private const val IMAGE_DIR = "image_manager/"
        private const val FILTER_DIR = "image_manager/filter/"
        private const val FILE_NAME_CAMERA = "camera_image.jpg"
        private const val FILE_NAME_ORIGIN = "origin_image.jpg"

        private fun CameraFilter.fileName(): String {
            return "filter_$id.jpg"
        }
    }
}
