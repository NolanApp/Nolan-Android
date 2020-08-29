package soup.nolan.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import soup.nolan.R
import soup.nolan.data.PlayRepository
import soup.nolan.databinding.CameraBinding
import soup.nolan.firebase.AppEvent
import soup.nolan.ui.EventObserver
import soup.nolan.ui.camera.CameraFragmentDirections.Companion.actionToEdit
import soup.nolan.ui.camera.CameraFragmentDirections.Companion.actionToPermission
import soup.nolan.ui.camera.CameraFragmentDirections.Companion.actionToPicker
import soup.nolan.ui.camera.CameraFragmentDirections.Companion.actionToSettings
import soup.nolan.ui.camera.filter.CameraFilterListAdapter
import soup.nolan.ui.review.ReviewViewModel
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.*
import soup.nolan.utils.hasCameraPermission
import soup.nolan.utils.hasRequiredPermissions
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.camera), CameraViewAnimation {

    @Inject
    lateinit var repository: PlayRepository
    private val reviewViewModel: ReviewViewModel by activityViewModels()

    private val viewModel: CameraViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    private var appEvent: AppEvent? = null
    private var binding: CameraBinding by autoCleared()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.filterPanel.isVisible) {
                binding.filterPanel.isVisible = false
            } else if (showInAppReview().not()) {
                activity?.finish()
            }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                context?.updateCameraUiAsPermission(binding)
            } else {
                toast(R.string.camera_error_permission)
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appEvent = AppEvent(context, "Camera")
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move).apply {
                interpolator = Interpolators.EASE_OUT_CUBIC
            }
        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move).apply {
                interpolator = Interpolators.EASE_OUT_CUBIC
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CameraBinding.bind(view).apply {
            view.context.initViewState(this)
            binding = this
        }
    }

    private fun Context.initViewState(binding: CameraBinding) {
        updateCameraUiAsPermission(binding)

        binding.cameraPermissionButton.setOnDebounceClickListener {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.header.run {
            settingsButton.setOnDebounceClickListener {
                findNavController().navigate(actionToSettings())
                appEvent?.sendButtonClick("more")
            }
            facingButton.setOnClickListener {
                viewModel.onLensFacingClick(facingButton.isLensFacingFront())
                appEvent?.sendButtonClick("lens_facing")
            }
            viewModel.lensFacingFront.observe(viewLifecycleOwner, Observer { lensFacingFront ->
                if (lensFacingFront) {
                    binding.cameraPreview.cameraLensFacing = LENS_FACING_FRONT
                } else {
                    binding.cameraPreview.cameraLensFacing = LENS_FACING_BACK
                }
                facingButton.setLensFacing(front = lensFacingFront)
            })
        }
        binding.footer.run {
            galleryButton.setOnDebounceClickListener {
                viewModel.onGalleryButtonClick()
                appEvent?.sendButtonClick("gallery")
            }
            captureButton.setOnDebounceClickListener {
                val saveFile = File(it.context.cacheDir, "capture")
                binding.cameraPreview.takePicture(
                    ImageCapture.OutputFileOptions.Builder(saveFile)
                        .setMetadata(ImageCapture.Metadata().apply {
                            isReversedHorizontal = binding.header.facingButton.isLensFacingFront()
                        })
                        .build(),
                    ContextCompat.getMainExecutor(it.context),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            findNavController().navigate(
                                actionToEdit(saveFile.toUri(), fromGallery = false, withSharedElements = true),
                                FragmentNavigatorExtras(captureButton to captureButton.transitionName)
                            )
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Timber.w(exception)
                        }
                    })
                binding.cameraShutterFlash.animateShutterFlash()
                appEvent?.sendButtonClick("capture")
            }
            filterButton.setOnDebounceClickListener {
                binding.filterPanel.isVisible = true
                appEvent?.sendButtonClick("filter")
            }
        }
        binding.run {
            filterPanelCloseButton.setOnClickListener {
                filterPanel.isVisible = false
            }

            val listAdapter = CameraFilterListAdapter {
                viewModel.onFilterSelected(it)
                cameraFilterDescription.run {
                    text = it.id
                    animateCameraFilterDescription()
                }
                cameraFilterDim.animateCameraFilterDim(target = cameraFilterDescription)
                appEvent?.sendFilterSelect(it.filter)
            }
            filterListView.adapter = listAdapter
            viewModel.allVisualFiltersLiveData.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
            })
            viewModel.selectedPosition.observe(viewLifecycleOwner, Observer {
                listAdapter.setSelectedPosition(it)
                filterListView.scrollToPositionInCenter(it)
            })
        }
        viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is CameraUiEvent.GoToGallery -> {
                    findNavController().navigate(actionToPicker())
                }
            }
        })
        systemViewModel.isHalfOpened.observe(viewLifecycleOwner, Observer { isHalfOpened ->
            (binding.root as? ConstraintLayout)?.let {
                val constraintSet = ConstraintSet().apply {
                    clone(it)
                    if (isHalfOpened) {
                        setVerticalBias(R.id.camera_preview, 0f)
                    } else {
                        setVerticalBias(R.id.camera_preview, .5f)
                    }
                }
                TransitionManager.beginDelayedTransition(it)
                constraintSet.applyTo(it)
            }
        })
    }

    private fun Context.updateCameraUiAsPermission(binding: CameraBinding) {
        val hasPermission = hasCameraPermission()
        binding.cameraPreview.isInvisible = hasPermission.not()
        binding.cameraPermissionButton.isGone = hasPermission
        binding.footer.captureButton.isInvisible = hasPermission.not()
        binding.header.facingButton.isInvisible = hasPermission.not()

        if (hasPermission) {
            binding.cameraPreview.post {
                startCameraWith(binding)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appEvent?.sendScreenEvent(this)
        if (context?.hasRequiredPermissions()?.not() == true) {
            findNavController().navigate(actionToPermission())
        } else {
            context?.updateCameraUiAsPermission(binding)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCameraWith(binding: CameraBinding) {
        binding.cameraPreview.bindToLifecycle(viewLifecycleOwner)
    }

    private fun showInAppReview(): Boolean {
        val reviewInfo = reviewViewModel.obtainReviewInfo()
        if (reviewInfo != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                repository.launchReview(requireActivity(), reviewInfo)
            }
            reviewViewModel.notifyAskedForReview()
        }
        return reviewInfo != null
    }
}
