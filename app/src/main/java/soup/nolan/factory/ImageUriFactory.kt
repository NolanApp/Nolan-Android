package soup.nolan.factory

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.model.CameraFilter
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
    fun getFilterImageUri(filter: CameraFilter): Uri
}

class ImageUriFactoryImpl(
    private val context: Context
) : ImageUriFactory {

    override fun createCameraImageUri(): Uri {
        val saveDir = File(context.filesDir, FILTER_FILE_PATH)
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }
        return FileProvider.getUriForFile(
            context,
            BuildConfig.FILES_AUTHORITY,
            File(saveDir, FILTER_FILE_CAMERA)
        )
    }

    override fun getDefaultImageUri(): Uri {
        return getImageUri(R.drawable.default_image)
    }

    override fun getFilterImageUri(filter: CameraFilter): Uri {
        val saveDir = File(context.filesDir, FILTER_FILE_PATH)
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }
        return File(saveDir, filter.toFileName()).toUri()
        //TODO: Generate thumbnails dynamically and use the file uris
    }

    private fun getImageUri(@DrawableRes resId: Int): Uri {
        return context.resources.run {
            Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getResourcePackageName(resId))
                .appendPath(getResourceTypeName(resId))
                .appendPath(getResourceEntryName(resId))
                .build()
        }
    }

    companion object {
        private const val FILTER_FILE_PATH = "image_manager/filter/"
        private const val FILTER_FILE_CAMERA = "camera_image.jpg"

        private fun CameraFilter.toFileName(): String {
            return "filter_$id.jpg"
        }
    }
}
