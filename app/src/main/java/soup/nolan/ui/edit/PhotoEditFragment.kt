package soup.nolan.ui.edit

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import soup.nolan.R
import soup.nolan.databinding.PhotoEditBinding
import soup.nolan.firebase.AppEvent
import soup.nolan.ui.EventObserver
import soup.nolan.ui.camera.filter.CameraFilterListAdapter
import soup.nolan.ui.camera.filter.CameraFilterViewModel
import soup.nolan.ui.edit.PhotoEditFragmentDirections.Companion.actionToCrop
import soup.nolan.ui.edit.crop.PhotoEditCropFragment
import soup.nolan.ui.edit.crop.PhotoEditCropFragment.Companion.KEY_REQUEST
import soup.nolan.ui.share.ShareListAdapter
import soup.nolan.ui.share.ShareViewModel
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.*

@AndroidEntryPoint
class PhotoEditFragment : Fragment(R.layout.photo_edit), PhotoEditViewAnimation {

    private val args: PhotoEditFragmentArgs by navArgs()
    private val viewModel: PhotoEditViewModel by viewModels()
    private val filterViewModel: CameraFilterViewModel by activityViewModels()
    private val shareViewModel: ShareViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    private var appEvent: AppEvent? = null
    private var binding: PhotoEditBinding by autoCleared()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.filterGroup.isVisible) {
                binding.renderUi(buttonIsVisible = true)
            } else {
                if (args.withSharedElements) {
                    binding.shutterStub.isVisible = true
                }
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
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move).apply {
                interpolator = Interpolators.EASE_IN_OUT_CUBIC
                duration = 500
                doOnEnd {
                    viewModel.onEnterAnimationDone()
                }
            }
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
        PhotoEditBinding.bind(view).apply {
            binding = this
            initViewState(this, view.context)

            viewModel.init(args.fileUri)

            if (savedInstanceState != null || args.withSharedElements.not()) {
                viewModel.onEnterAnimationDone()
            }
        }
    }

    private fun initViewState(binding: PhotoEditBinding, context: Context) {
        binding.run {
            editableImage.setOnScaleChangeListener { _, _, _ ->
                viewModel.onZoomChanged(zoomIn = editableImage.scale > 1.05f)
            }

            cropButton.isVisible = args.fromGallery
            cropButton.setOnDebounceClickListener {
                viewModel.onCropClick()
                appEvent?.sendButtonClick("crop")
            }
            filterButton.setOnDebounceClickListener {
                renderUi(filterIsVisible = true)
                appEvent?.sendButtonClick("filter")
            }
            saveButton.setOnDebounceClickListener {
                viewModel.onSaveClick()
                appEvent?.sendButtonClick("save")
            }
            shareButton.setOnDebounceClickListener {
                viewModel.onShareClick()
                appEvent?.sendButtonClick("share")
            }
            dim.setOnClickListener {
                renderUi(buttonIsVisible = true)
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
            viewModel.isZoomIn.observe(viewLifecycleOwner, Observer { isZoomIn ->
                zoomScrim.animateVisible(isZoomIn)

                val tintColor = if (isZoomIn) {
                    Color.WHITE
                } else {
                    context.getColorAttr(R.attr.colorOnSurface)
                }
                val tint = ColorStateList.valueOf(tintColor)
                cropButton.imageTintList = tint
                faceBlurButton.imageTintList = tint
                filterButton.imageTintList = tint
                saveButton.imageTintList = tint
                shareButton.imageTintList = tint
            })
            viewModel.isShutterVisible.observe(viewLifecycleOwner, Observer {
                shutterStub.isVisible = it
            })
            viewModel.buttonPanelIsShown.observe(viewLifecycleOwner, Observer {
                renderUi(buttonIsVisible = it)
            })
            viewModel.bitmap.observe(viewLifecycleOwner, Observer {
                editableImage.setImageBitmap(it)
            })
            viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    is PhotoEditUiEvent.Save -> lifecycleScope.launch {
                        Gallery.saveBitmap(context, it.bitmap)
                    }
                    is PhotoEditUiEvent.Share -> activity?.let { activity ->
                        it.uiModel.share(activity, it.shareImageUri)
                    }
                    is PhotoEditUiEvent.GoToCrop -> {
                        findNavController().navigate(actionToCrop(it.fileUri, it.cropRect))
                    }
                    is PhotoEditUiEvent.ShowShare -> {
                        renderUi(shareIsVisible = true)
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

                renderUi(buttonIsVisible = true)
                appEvent?.sendFilterSelect(it.filter)
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
                viewModel.onShareItemClick(it, editableImage.drawable)
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
        appEvent?.sendScreenEvent(this)
    }

    private fun PhotoEditBinding.renderUi(
        buttonIsVisible: Boolean = false,
        filterIsVisible: Boolean = false,
        shareIsVisible: Boolean = false
    ) {
        buttonPanel.animateVisible(buttonIsVisible)
        filterGroup.isVisible = filterIsVisible
        shareGroup.isVisible = shareIsVisible
        dim.isVisible = filterIsVisible || shareIsVisible
    }
}
