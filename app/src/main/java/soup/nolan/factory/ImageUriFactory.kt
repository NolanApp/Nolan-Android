package soup.nolan.factory

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
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
        //TODO: Generate thumbnails dynamically and use the file uris
        return getImageUri(filter.thumbnailResId)
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

    private val CameraFilter.thumbnailResId: Int
        @DrawableRes
        get() = when (id) {
            CameraFilter.OR.id -> R.drawable.default_image
            CameraFilter.A01.id -> R.drawable.a01
            CameraFilter.A02.id -> R.drawable.a02
            CameraFilter.A03.id -> R.drawable.a03
            CameraFilter.A04.id -> R.drawable.a04
            CameraFilter.A05.id -> R.drawable.a05
            CameraFilter.A06.id -> R.drawable.a06
            CameraFilter.A07.id -> R.drawable.a07
            CameraFilter.A08.id -> R.drawable.a08
            CameraFilter.A09.id -> R.drawable.a09
            CameraFilter.A10.id -> R.drawable.a10
            CameraFilter.A11.id -> R.drawable.a11
            CameraFilter.A12.id -> R.drawable.a12
            CameraFilter.A13.id -> R.drawable.a13
            CameraFilter.A14.id -> R.drawable.a14
            CameraFilter.A15.id -> R.drawable.a15
            CameraFilter.A16.id -> R.drawable.a16
            CameraFilter.A17.id -> R.drawable.a17
            CameraFilter.A18.id -> R.drawable.a18
            CameraFilter.A19.id -> R.drawable.a19
            CameraFilter.A20.id -> R.drawable.a20
            CameraFilter.A21.id -> R.drawable.a21
            CameraFilter.A22.id -> R.drawable.a22
            CameraFilter.A23.id -> R.drawable.a23
            CameraFilter.A24.id -> R.drawable.a24
            CameraFilter.A25.id -> R.drawable.a25
            CameraFilter.A26.id -> R.drawable.a26
            else -> throw IllegalArgumentException("CameraFilter($id) is invalid.")
        }

    companion object {
        private const val FILTER_FILE_PATH = "image_manager/filter/"
        private const val FILTER_FILE_CAMERA = "camera_image.jpg"
        private val FILTER_FILE_ORIGIN = "filter_${CameraFilter.OR.id}.jpg"
    }
}
