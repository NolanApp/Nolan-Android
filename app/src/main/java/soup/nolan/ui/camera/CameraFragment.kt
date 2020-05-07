package soup.nolan.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import kotlinx.coroutines.launch
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.ads.AdManager
import soup.nolan.databinding.CameraBinding
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.camera.filter.CameraFilterListAdapter
import soup.nolan.ui.camera.filter.CameraFilterViewModel
import soup.nolan.ui.edit.Gallery
import soup.nolan.ui.utils.autoCleared
import soup.nolan.ui.utils.scrollToPositionInCenter
import soup.nolan.ui.utils.setOnDebounceClickListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class CameraFragment : BaseFragment(R.layout.camera) {

    @Inject
    lateinit var adManager: AdManager

    private val viewModel: CameraViewModel by viewModel()
    private val filterViewModel: CameraFilterViewModel by activityViewModel()

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
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(CameraBinding.bind(view)) {
            initViewState(this)
            binding = this
        }
    }

    private fun initViewState(binding: CameraBinding) {
        if (allPermissionsGranted(binding.root.context)) {
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
            }
            facingButton.setOnClickListener {
                viewModel.onLensFacingClick(facingButton.isLensFacingFront())
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
                if (BuildConfig.DEBUG) {
                    lifecycleScope.launch {
                        adManager.loadRewardedAd()?.let {
                            if (it.isLoaded) {
                                val adCallback = object : RewardedAdCallback() {
                                    override fun onRewardedAdOpened() {
                                        Timber.d("onRewardedAdOpened:")
                                    }

                                    override fun onRewardedAdClosed() {
                                        Timber.d("onRewardedAdClosed:")
                                        Gallery.takePicture(this@CameraFragment)
                                        //TODO: reload rewarded ad
                                    }

                                    override fun onUserEarnedReward(reward: RewardItem) {
                                        Timber.i("onUserEarnedReward: amount=${reward.amount}")
                                    }

                                    override fun onRewardedAdFailedToShow(errorCode: Int) {
                                        Timber.w("onRewardedAdFailedToShow: errorCode=$errorCode")
                                    }
                                }
                                it.show(activity, adCallback)
                            }
                        }
                    }
                } else {
                    Gallery.takePicture(this@CameraFragment)
                }
            }
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
            }
            filterButton.setOnDebounceClickListener {
                binding.filterListView.isVisible = true
                backPressedCallback.isEnabled = true
            }

            val listAdapter = CameraFilterListAdapter {
                filterViewModel.onFilterSelect(it)
            }
            binding.filterListView.adapter = listAdapter
            filterViewModel.filterList.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it.list)
            })
            filterViewModel.selectedPosition.observe(viewLifecycleOwner, Observer {
                listAdapter.setSelectedPosition(it)
                binding.filterListView.scrollToPositionInCenter(it)
            })
        }
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
                Toast.makeText(context, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
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
