package soup.nolan.ui.edit

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import soup.nolan.R
import soup.nolan.analytics.AppEvent
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
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.autoCleared
import soup.nolan.ui.utils.scrollToPositionInCenter
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast

class PhotoEditFragment : BaseFragment(R.layout.photo_edit), PhotoEditViewAnimation {

    private lateinit var appEvent: AppEvent

    private val args: PhotoEditFragmentArgs by navArgs()
    private val viewModel: PhotoEditViewModel by viewModel()
    private val filterViewModel: CameraFilterViewModel by activityViewModel()
    private val shareViewModel: ShareViewModel by viewModel()
    private val systemViewModel: SystemViewModel by activityViewModels()

    private var binding: PhotoEditBinding by autoCleared()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.filterGroup.isVisible) {
                binding.filterGroup.isVisible = false
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appEvent = AppEvent(context, "PhotoEdit")
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
                appEvent.sendButtonClick("crop")
            }
            filterButton.setOnDebounceClickListener {
                filterGroup.isVisible = true
                appEvent.sendButtonClick("filter")
            }
            filterDim.setOnClickListener {
                filterGroup.isVisible = false
            }
            saveButton.setOnDebounceClickListener {
                viewModel.onSaveClick()
                appEvent.sendButtonClick("save")
            }
            shareButton.setOnDebounceClickListener {
                //TODO:
                //viewModel.onShareClick()
                editableImage.drawable?.let {
                    onShare(context, it)
                }
                appEvent.sendButtonClick("share")
            }
            shareDim.setOnClickListener {
                shareGroup.isVisible = false
            }

            viewModel.isLoading.observe(viewLifecycleOwner, Observer {
                if (it) {
                    loadingView.show()
                    loadingHint.animateIn()
                } else {
                    loadingHint.animateOut {
                        loadingView.hide()
                    }
                }
            })
            viewModel.buttonPanelIsShown.observe(viewLifecycleOwner, Observer {
                buttonPanel.animateVisible(it)
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
                    is PhotoEditUiEvent.ShowErrorToast -> {
                        toast(it.message)
                    }
                }
            })

            val filterListAdapter = CameraFilterListAdapter {
                filterViewModel.onFilterSelect(it)
                viewModel.changeFilter(it.filter)

                filterGroup.isVisible = false
                appEvent.sendFilterSelect(it.filter)
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

            systemViewModel.isHalfOpened.observe(viewLifecycleOwner, Observer { isHalfOpened ->
                (root as? ConstraintLayout)?.let {
                    val constraintSet = ConstraintSet().apply {
                        clone(it)
                        if (isHalfOpened) {
                            setVerticalBias(R.id.editable_image, 0f)
                        } else {
                            setVerticalBias(R.id.editable_image, .5f)
                        }
                    }
                    TransitionManager.beginDelayedTransition(it)
                    constraintSet.applyTo(it)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        appEvent.sendScreenEvent(this)
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
