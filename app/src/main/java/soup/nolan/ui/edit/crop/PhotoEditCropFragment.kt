package soup.nolan.ui.edit.crop

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.theartofdev.edmodo.cropper.CropImageView
import soup.nolan.R
import soup.nolan.analytics.AppEvent
import soup.nolan.databinding.PhotoEditCropBinding
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.edit.Gallery
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast
import timber.log.Timber

class PhotoEditCropFragment : BaseFragment(R.layout.photo_edit_crop) {

    private lateinit var appEvent: AppEvent

    private val args: PhotoEditCropFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appEvent = AppEvent(context, "PhotoCrop")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(PhotoEditCropBinding.bind(view)) {
            initViewState(this)
        }
    }

    private fun initViewState(binding: PhotoEditCropBinding) {
        binding.run {
            cropImageView.setOnSetImageUriCompleteListener { view, _, error ->
                if (error == null) {
                    // 특정 형태로 Crop 된 상태 노출 처리
                    view.cropRect = args.cropRect
                }
            }
            cropImageView.setOnCropImageCompleteListener { _, result ->
                saveResult(result)
            }
            cropImageView.setImageUriAsync(args.fileUri)

            submitButton.setOnDebounceClickListener {
                appEvent.sendButtonClick("crop_submit")
                val saveFileUri = Gallery.createCacheFileUri(it.context, "cropped.png")
                cropImageView.saveCroppedImageAsync(saveFileUri)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appEvent.sendScreenEvent(this)
    }

    private fun saveResult(result: CropImageView.CropResult) {
        if (result.isSuccessful && result.uri != null) {
            setFragmentResult(
                KEY_REQUEST, bundleOf(
                    EXTRA_FILE_URI to result.uri,
                    EXTRA_CROP_RECT to result.cropRect
                )
            )
            findNavController().navigateUp()
        } else {
            toast("Cropping is failed...")
            Timber.w(result.error, "handleCropResult: ")
        }
    }

    companion object {
        const val KEY_REQUEST = "request_photo_crop"
        const val EXTRA_FILE_URI = "fileUri"
        const val EXTRA_CROP_RECT = "cropRect"
    }
}