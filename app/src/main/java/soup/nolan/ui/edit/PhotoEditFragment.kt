package soup.nolan.ui.edit

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.nolan.R
import soup.nolan.databinding.PhotoEditBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.edit.PhotoEditFragmentDirections.Companion.actionToCrop
import soup.nolan.ui.edit.PhotoEditFragmentDirections.Companion.actionToShare
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast

class PhotoEditFragment : BaseFragment(R.layout.photo_edit) {

    private val args: PhotoEditFragmentArgs by navArgs()
    private val viewModel: PhotoEditViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(PhotoEditBinding.bind(view)) {
            saveButton.setOnDebounceClickListener {
                viewModel.onSaveClick()
            }
            shareButton.setOnDebounceClickListener {
                viewModel.onShareClick()
            }

            viewModel.isLoading.observe(viewLifecycleOwner, Observer {
                if (it) {
                    loadingView.show()
                } else {
                    loadingView.hide()
                }
            })
            viewModel.bitmap.observe(viewLifecycleOwner, Observer {
                editableImage.setImageBitmap(it)
            })
            viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    is PhotoEditUiEvent.Save -> Gallery.saveBitmap(view.context, it.bitmap)
                    is PhotoEditUiEvent.GoToCrop ->
                        findNavController().navigate(actionToCrop(it.fileUri))
                    is PhotoEditUiEvent.GoToShare ->
                        findNavController().navigate(actionToShare(it.fileUri))
                    is PhotoEditUiEvent.ShowToast ->
                        toast(it.message)
                }
            })
        }

        if (savedInstanceState == null) {
            val input = FirebaseVisionImage.fromFilePath(view.context, args.fileUri).bitmap
            viewModel.init(args.fileUri, input, args.fromGallery)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //TODO:
    }
}
