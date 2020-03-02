package soup.nolan.ui.camera

import android.Manifest
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.impl.utils.executor.CameraXExecutors
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import soup.nolan.R
import soup.nolan.core.detector.FaceDetector
import soup.nolan.core.detector.firebase.FirebaseFaceDetector
import soup.nolan.core.detector.model.Frame
import soup.nolan.databinding.CameraFragmentBinding
import soup.nolan.model.Face
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.edit.Gallery
import soup.nolan.ui.utils.lazyFast
import soup.nolan.ui.utils.setOnDebounceClickListener
import timber.log.Timber
import java.io.File
import kotlin.math.max
import kotlin.math.min

class CameraFragment : BaseFragment() {

    private val viewModel: CameraViewModel by viewModel()

    private lateinit var binding: CameraFragmentBinding

    private val faceImageAnalyzer by lazyFast {
        val detector: FaceDetector = FirebaseFaceDetector().apply {
            setCallback(object : FaceDetector.Callback {

                override fun onDetecting(frame: Frame) {
                    Timber.d("onDetecting:")
                    if (startRequested.not()) {
                        startRequested = true

                        val min = min(frame.width, frame.height)
                        val max = max(frame.width, frame.height)
                        binding.faceBlurView.setCameraInfo(min, max)
                        binding.faceBlurView.clear()
                    }
                }

                override fun onDetected(originalImage: Bitmap, faceList: List<Face>) {
                    Timber.d("onDetected: count=${faceList.size}")
                    binding.faceBlurView.renderFaceList(originalImage, faceList)
                }

                override fun onDetectFailed() {
                    binding.faceBlurView.run {
                        clear()
                        postInvalidate()
                    }
                }
            })
        }
        FaceImageAnalyzer(detector).apply {
            isMirror = binding.cameraPreview.cameraLensFacing == LENS_FACING_FRONT
        }
    }

    private val gpuImageAnalyzer by lazyFast {
        GpuImageAnalyzer {
            binding.gpuImageView.setImage(it)
        }.apply {
            isMirror = binding.cameraPreview.cameraLensFacing == LENS_FACING_FRONT
        }
    }

    private var startRequested = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CameraFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initViewState(binding)
        return binding.root
    }

    private fun initViewState(binding: CameraFragmentBinding) {
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

        binding.gpuImageView.filter = GPUImageSepiaToneFilter()
//        binding.randomButton.setOnClickListener {
//            binding.gpuImageView.filter = when (Random.nextInt() % 3) {
//                0 -> GPUImageSepiaToneFilter()
//                1 -> GPUImageSketchFilter()
//                2 -> GPUImageGrayscaleFilter()
//                else -> GPUImageGaussianBlurFilter()
//            }
//        }
        binding.header.run {
            moreButton.setOnDebounceClickListener {
                findNavController().navigate(CameraFragmentDirections.actionToSettings())
            }
            ratioButton.setOnClickListener {}

            val flipOut = AnimatorInflater.loadAnimator(root.context, R.animator.flip_out)
            val flipIn = AnimatorInflater.loadAnimator(root.context, R.animator.flip_in)

            facingButton.setOnClickListener {
                binding.cameraPreview.toggleCamera()
                binding.cameraPreview.cameraLensFacing?.let { lensFacing ->
                    val isFrontLens = lensFacing == LENS_FACING_FRONT
                    if (!isFrontLens) {
                        flipOut.setTarget(facingFrontButton)
                        flipIn.setTarget(facingBackButton)
                        flipOut.start()
                        flipIn.start()
                    } else {
                        flipOut.setTarget(facingBackButton)
                        flipIn.setTarget(facingFrontButton)
                        flipOut.start()
                        flipIn.start()
                    }
                    facingButton.isSelected = !isFrontLens
                    faceImageAnalyzer.isMirror = isFrontLens
                    gpuImageAnalyzer.isMirror = isFrontLens
                }
            }
        }
        binding.filter.run {
            val listAdapter = CameraFilterListAdapter {
                //TODO: 클릭 처리
            }
//            listAdapter.submitList(Styles.thumbnails.mapIndexed(::CameraFilterUiModel))
            listView.adapter = listAdapter
        }
        binding.footer.run {
            galleryButton.setOnDebounceClickListener {
                Gallery.takePicture(this@CameraFragment)
            }
            captureButton.setOnDebounceClickListener {
                val saveFile = File(it.context.cacheDir, "capture")
                binding.cameraPreview.takePicture(
                    saveFile,
                    CameraXExecutors.mainThreadExecutor(),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            findNavController().navigate(CameraFragmentDirections.actionToEdit(saveFile.toUri()))
                        }

                        override fun onError(exception: ImageCaptureException) {
                            //TODO
                        }
                    })
            }
            filterButton.setOnDebounceClickListener {
                binding.filter.root.run {
                    isVisible = !isVisible
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Gallery.onPictureTaken(requestCode, resultCode, data) {
            findNavController().navigate(CameraFragmentDirections.actionToEdit(it))
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCameraWith(binding: CameraFragmentBinding) {
        //binding.cameraPreview.setAnalyzer(faceImageAnalyzer)
        //binding.cameraPreview.setAnalyzer(gpuImageAnalyzer)
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
                Toast.makeText(context, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
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
