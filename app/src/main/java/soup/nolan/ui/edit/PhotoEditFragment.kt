package soup.nolan.ui.edit

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import soup.nolan.R
import soup.nolan.databinding.PhotoEditBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.camera.filter.CameraFilterListAdapter
import soup.nolan.ui.camera.filter.CameraFilterViewModel
import soup.nolan.ui.edit.PhotoEditFragmentDirections.Companion.actionToCrop
import soup.nolan.ui.edit.crop.PhotoEditCropFragment
import soup.nolan.ui.edit.crop.PhotoEditCropFragment.Companion.KEY_REQUEST
import soup.nolan.ui.share.ShareImageFactory
import soup.nolan.ui.share.ShareListAdapter
import soup.nolan.ui.share.ShareViewModel
import soup.nolan.ui.utils.autoCleared
import soup.nolan.ui.utils.scrollToPositionInCenter
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast

class PhotoEditFragment : BaseFragment(R.layout.photo_edit) {

    private val args: PhotoEditFragmentArgs by navArgs()
    private val viewModel: PhotoEditViewModel by viewModel()
    private val filterViewModel: CameraFilterViewModel by activityViewModel()
    private val shareViewModel: ShareViewModel by viewModel()

    private var binding: PhotoEditBinding by autoCleared()

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (binding.filterGroup.isVisible) {
                binding.filterGroup.isVisible = false
                isEnabled = false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(KEY_REQUEST) { _, bundle ->
            val fileUri: Uri? = bundle.getParcelable(PhotoEditCropFragment.EXTRA_FILE_URI)
            if (fileUri != null) {
                val cropRect: Rect? = bundle.getParcelable(PhotoEditCropFragment.EXTRA_CROP_RECT)
                viewModel.update(fileUri, cropRect)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(PhotoEditBinding.bind(view)) {
            binding = this
            initViewState(this, view.context)
        }
        viewModel.init(args.fileUri)
    }

    private fun initViewState(binding: PhotoEditBinding, context: Context) {
        binding.run {
            cropButton.isVisible = args.fromGallery
            cropButton.setOnDebounceClickListener {
                viewModel.onCropClick()
            }
            filterButton.setOnDebounceClickListener {
                filterGroup.isVisible = true
                backPressedCallback.isEnabled = true
            }
            filterDim.setOnClickListener {
                filterGroup.isVisible = false
                backPressedCallback.isEnabled = false
            }
            saveButton.setOnDebounceClickListener {
                viewModel.onSaveClick()
            }
            shareButton.setOnDebounceClickListener {
                //TODO:
                //viewModel.onShareClick()
                editableImage.drawable?.let {
                    onShare(context, it)
                }
            }
            shareDim.setOnClickListener {
                shareGroup.isVisible = false
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
                    is PhotoEditUiEvent.Save -> lifecycleScope.launch {
                        Gallery.saveBitmap(context, it.bitmap)
                    }
                    is PhotoEditUiEvent.GoToCrop -> {
                        findNavController().navigate(actionToCrop(it.fileUri, it.cropRect))
                    }
                    is PhotoEditUiEvent.ShowShare -> {
                        shareGroup.isVisible = true
                    }
                    is PhotoEditUiEvent.ShowToast -> {
                        toast(it.message)
                    }
                }
            })

            val filterListAdapter = CameraFilterListAdapter {
                filterViewModel.onFilterSelect(it)
                viewModel.changeFilter(it.filter)

                filterGroup.isVisible = false
                backPressedCallback.isEnabled = false
            }
            filterListView.adapter = filterListAdapter
            filterViewModel.filterList.observe(viewLifecycleOwner, Observer {
                filterListAdapter.submitList(it.list)
            })
            filterViewModel.selectedPosition.observe(viewLifecycleOwner, Observer {
                filterListAdapter.setSelectedPosition(it)
                filterListView.scrollToPositionInCenter(it)
            })

            val listAdapter = ShareListAdapter {
                //shareViewModel.onShareClick(it)
            }
            shareListView.adapter = listAdapter
            shareViewModel.shareList.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
            })
        }
    }

    private fun onShare(context: Context, drawable: Drawable) {
        val uriToImage = ShareImageFactory.createShareImageUri(context, drawable) ?: return
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uriToImage)
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.share)))
    }
}
