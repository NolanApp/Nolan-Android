package soup.nolan.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import soup.nolan.R
import soup.nolan.analytics.AppEvent
import soup.nolan.databinding.CameraBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.ResultContract
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.camera.filter.CameraFilterListAdapter
import soup.nolan.ui.camera.filter.CameraFilterViewModel
import soup.nolan.ui.edit.Gallery
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.autoCleared
import soup.nolan.ui.utils.scrollToPositionInCenter
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast
import timber.log.Timber
import java.io.File

class CameraFragment : BaseFragment(R.layout.camera), CameraViewAnimation {

    private lateinit var appEvent: AppEvent

    private val viewModel: CameraViewModel by viewModel()
    private val filterViewModel: CameraFilterViewModel by activityViewModel()
    private val systemViewModel: SystemViewModel by activityViewModels()

    private var binding: CameraBinding by autoCleared()

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (binding.filterListView.isVisible) {
                binding.filterListView.isVisible = false
                isEnabled = false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appEvent = AppEvent(context, "Camera")
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ResultContract.CAMERA) { _, bundle ->
            val showAds = bundle.getBoolean(ResultContract.CAMERA_EXTRA_SHOW_ADS, false)
            if (showAds) {
                viewModel.onShowAdClick()
                appEvent.sendButtonClick("show_ad")
            }
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
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.header.run {
            moreButton.setOnDebounceClickListener {
                findNavController().navigate(CameraFragmentDirections.actionToSettings())
                appEvent.sendButtonClick("more")
            }
            facingButton.setOnClickListener {
                viewModel.onLensFacingClick(facingButton.isLensFacingFront())
                appEvent.sendButtonClick("lens_facing")
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
                appEvent.sendButtonClick("gallery")
            }
            viewModel.gallerySelectableCount.observe(viewLifecycleOwner, Observer {
                currentCount.text = it.toString()
            })
            viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    is CameraUiEvent.ShowAdDialog -> {
                        findNavController().navigate(CameraFragmentDirections.actionToAds())
                    }
                    is CameraUiEvent.ShowAd -> {
                        it.rewardedAd.show(activity, object : RewardedAdCallback() {
                            override fun onRewardedAdOpened() {
                                Timber.d("onRewardedAdOpened:")
                            }

                            override fun onRewardedAdClosed() {
                                Timber.d("onRewardedAdClosed:")
                                viewModel.onRewardedAdClosed()
                            }

                            override fun onUserEarnedReward(reward: RewardItem) {
                                Timber.i("onUserEarnedReward: amount=${reward.amount}")
                                viewModel.onUserEarnedReward(reward.amount)
                                appEvent.sendPromotionEvent("earned_reward")
                            }

                            override fun onRewardedAdFailedToShow(errorCode: Int) {
                                Timber.w("onRewardedAdFailedToShow: errorCode=$errorCode")
                                toast(R.string.camera_error_network)
                                viewModel.onRewardedAdFailedToShow()
                            }
                        })
                    }
                    is CameraUiEvent.ShowErrorToast -> {
                        toast(R.string.camera_error_network)
                    }
                    is CameraUiEvent.GoToGallery -> {
                        Gallery.takePicture(this@CameraFragment)
                    }
                }
            })
            captureButton.setOnDebounceClickListener {
                val saveFile = File(it.context.cacheDir, "capture")
                binding.cameraPreview.takePicture(
                    saveFile,
                    ContextCompat.getMainExecutor(it.context),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            findNavController().navigate(
                                CameraFragmentDirections.actionToEdit(
                                    saveFile.toUri(),
                                    false
                                )
                            )
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Timber.w(exception)
                        }
                    })
                appEvent.sendButtonClick("capture")
            }
            filterButton.setOnDebounceClickListener {
                backPressedCallback.isEnabled = true
                binding.filterPanel.isVisible = true
                appEvent.sendButtonClick("filter")
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
                appEvent.sendFilterSelect(it.filter)
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
        appEvent.sendScreenEvent(this)
        viewModel.refresh()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Gallery.onPictureTaken(requestCode, resultCode, data) {
            findNavController().navigate(CameraFragmentDirections.actionToEdit(it, true))
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCameraWith(binding: CameraBinding) {
        //binding.cameraPreview.setAnalyzer(faceImageAnalyzer)
        binding.cameraPreview.bindToLifecycle(viewLifecycleOwner)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val context: Context = binding.root.context
            if (allPermissionsGranted(context)) {
                startCameraWith(binding)
            } else {
                toast(R.string.camera_error_permission)
                findNavController().popBackStack()
            }
        }
    }

    private fun allPermissionsGranted(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {

        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
