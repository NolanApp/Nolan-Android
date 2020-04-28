package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.databinding.PhotoEditBinding
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.stylize.popart.PopStyleTransfer
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.edit.PhotoEditFragmentDirections.Companion.actionToShare
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast
import timber.log.Timber

class PhotoEditFragment : BaseFragment(R.layout.photo_edit) {

    private val args: PhotoEditFragmentArgs by navArgs()
    private val viewModel: PhotoEditViewModel by viewModel()

    private val transfer by lazy { PopStyleTransfer() }
    private val legacyTransfer by lazy { LegacyStyleTransfer(requireContext()) }

    private var lastBitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(PhotoEditBinding.bind(view)) {
            loadingView.show()
            saveButton.setOnDebounceClickListener {
                val bitmap = lastBitmap
                if (bitmap != null) {
                    Gallery.saveBitmap(it.context, bitmap)
                }
            }
            shareButton.setOnDebounceClickListener {
                findNavController().navigate(actionToShare(args.fileUri))
            }

            val input = FirebaseVisionImage.fromFilePath(requireContext(), args.fileUri).bitmap
            editableImage.setImageBitmap(input)
            lastBitmap = input
            val start = System.currentTimeMillis()
            legacyTransfer.transform(input)
                .addOnSuccessListener {
                    val duration = System.currentTimeMillis() - start
                    Timber.d("success: $it $duration ms")
                    lastBitmap = it
                    activity?.runOnUiThread {
                        editableImage.setImageBitmap(it)
                    }
                    if (BuildConfig.DEBUG) {
                        toast("Success! ($duration ms)")
                    }
                }
                .addOnFailureListener {
                    Timber.d("failure: $it")
                    if (BuildConfig.DEBUG) {
                        toast("Error: $it")
                    }
                }
                .addOnCompleteListener {
                    loadingView.hide()
                }
        }
    }
}
