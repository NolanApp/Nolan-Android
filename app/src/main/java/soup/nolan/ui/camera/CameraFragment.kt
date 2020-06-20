package soup.nolan.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import soup.nolan.R
import soup.nolan.analytics.AppEvent
import soup.nolan.databinding.CameraBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.camera.CameraFragmentDirections.Companion.actionToEdit
import soup.nolan.ui.camera.CameraFragmentDirections.Companion.actionToSettings
import soup.nolan.ui.camera.filter.CameraFilterListAdapter
import soup.nolan.ui.camera.filter.CameraFilterViewModel
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.*
import timber.log.Timber
import java.io.File

class CameraFragment : Fragment(R.layout.camera), CameraViewAnimation {

    private val viewModel: CameraViewModel by viewModels()
    private val filterViewModel: CameraFilterViewModel by activityViewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()

    private var appEvent: AppEvent? = null
    private var binding: CameraBinding by autoCleared()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.filterPanel.isVisible) {
                binding.filterPanel.isVisible = false
            } else {
                activity?.finish()
            }
        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                findNavController().navigate(
                    actionToEdit(uri, true),
                    FragmentNavigatorExtras(binding.footer.captureButton.let { it to it.transitionName })
                )
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val context: Context = binding.root.context
            if (allPermissionsGranted(context)) {
                startCameraWith(binding)
            } else {
                toast(R.string.camera_error_permission)
                findNavController().popBackStack()
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
        with(CameraBinding.bind(view)) {
            initViewState(this, view.context)
            binding = this
        }
    }

    private fun initViewState(binding: CameraBinding, context: Context) {
        if (allPermissionsGranted(context)) {
            binding.cameraPreview.post {
                startCameraWith(binding)
            }
        } else {
            permissionLauncher.launch(REQUIRED_PERMISSIONS)
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
            viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    is CameraUiEvent.GoToGallery -> {
                        galleryLauncher.launch("image/*")
                    }
                }
            })
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
                                actionToEdit(saveFile.toUri(), false),
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
                filterViewModel.onFilterSelect(it)
                cameraFilterDescription.run {
                    text = it.id
                    animateCameraFilterDescription()
                }
                cameraFilterDim.animateCameraFilterDim(target = cameraFilterDescription)
                appEvent?.sendFilterSelect(it.filter)
            }
            filterListView.adapter = listAdapter
            filterViewModel.filterList.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it.list)
            })
            filterViewModel.selectedPosition.observe(viewLifecycleOwner, Observer {
                listAdapter.setSelectedPosition(it)
                filterListView.scrollToPositionInCenter(it)
            })
        }
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

    override fun onResume() {
        super.onResume()
        appEvent?.sendScreenEvent(this)
    }

    @SuppressLint("MissingPermission")
    private fun startCameraWith(binding: CameraBinding) {
        binding.cameraPreview.bindToLifecycle(viewLifecycleOwner)
    }

    private fun allPermissionsGranted(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
