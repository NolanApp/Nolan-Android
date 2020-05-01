package soup.nolan.ui.edit.crop

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.theartofdev.edmodo.cropper.CropImageView
import soup.nolan.R
import soup.nolan.databinding.PhotoEditCropBinding
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.edit.Gallery
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast
import timber.log.Timber

class PhotoEditCropFragment : BaseFragment(R.layout.photo_edit_crop) {

    private val args: PhotoEditCropFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = args.fileUri
        with(PhotoEditCropBinding.bind(view)) {
            cropImageView.setOnSetImageUriCompleteListener { view, _, error ->
                if (error == null) {
                    // 특정 형태로 Crop 된 상태 노출 처리
                    view.cropRect = args.cropRect
                }
            }
            cropImageView.setOnCropImageCompleteListener { _, result ->
                handleCropResult(result)
            }
            cropImageView.setImageUriAsync(uri)

            submitButton.setOnDebounceClickListener {
                val saveFileUri = Gallery.createCacheFileUri(it.context, "cropped.png")
                cropImageView.saveCroppedImageAsync(saveFileUri)
            }
        }
    }

    private fun handleCropResult(result: CropImageView.CropResult) {
        if (result.isSuccessful && result.uri != null) {
            val intent = Intent().apply {
                putExtra(
                    EXTRA_SAVE_ITEM,
                    CroppedPhoto(
                        fileUri = result.uri,
                        cropRect = result.cropRect
                    )
                )
            }
//            setResult(Activity.RESULT_OK, intent)
//            finish()
            Timber.d("handleCropResult: $intent")
            findNavController().navigateUp()
        } else {
            toast("crop이 실패했습니다.")
            Timber.w(result.error, "handleCropResult: ")
        }
    }

    companion object {
        const val EXTRA_SAVE_ITEM = "image.save"
    }
}
