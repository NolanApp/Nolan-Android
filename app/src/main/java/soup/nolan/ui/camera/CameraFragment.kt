package soup.nolan.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Rational
import android.view.*
import android.widget.Toast
import androidx.camera.core.CameraX
import androidx.camera.core.CameraX.LensFacing
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysisConfig
import androidx.camera.core.PreviewConfig
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import soup.nolan.core.detector.FaceDetector
import soup.nolan.core.detector.firebase.FirebaseFaceDetector
import soup.nolan.core.detector.model.Frame
import soup.nolan.databinding.CameraFragmentBinding
import soup.nolan.model.Face
import soup.nolan.ui.base.BaseFragment
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class CameraFragment : BaseFragment() {

    private val viewModel: CameraViewModel by viewModel()

    private lateinit var binding: CameraFragmentBinding

    private var startRequested = false

    private val lensFacing = LensFacing.BACK

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
        binding.randomButton.setOnClickListener {
            binding.gpuImageView.filter = when (Random.nextInt() % 3) {
                0 -> GPUImageSepiaToneFilter()
                1 -> GPUImageSketchFilter()
                2 -> GPUImageGrayscaleFilter()
                else -> GPUImageGaussianBlurFilter()
            }
        }
        binding.footer.captureButton.setOnClickListener {
            findNavController().navigate(CameraFragmentDirections.actionToEdit())
        }
    }

    private fun startCameraWith(binding: CameraFragmentBinding) {
        val textureView: TextureView = binding.cameraPreview
        val metrics = DisplayMetrics().also { textureView.display.getRealMetrics(it) }
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        val previewConfig = PreviewConfig.Builder()
            .apply {
                setLensFacing(lensFacing)
                setTargetAspectRatio(screenAspectRatio)
                setTargetRotation(textureView.display.rotation)
            }
            .build()
        val previewUseCase = AutoFitPreviewBuilder.build(previewConfig, textureView)

        val detector: FaceDetector = FirebaseFaceDetector().apply {
            setCallback(object : FaceDetector.Callback {

                override fun onDetecting(frame: Frame) {
                    if (startRequested.not()) {
                        startRequested = true

                        val min = min(frame.width, frame.height)
                        val max = max(frame.width, frame.height)
                        binding.faceBlurView.setCameraInfo(min, max)
                        binding.faceBlurView.clear()
                    }
                }

                override fun onDetected(originalImage: Bitmap, faceList: List<Face>) {
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
        val analyzerConfig = ImageAnalysisConfig.Builder()
            .apply {
                setLensFacing(lensFacing)
                val analyzerThread = HandlerThread("FilterAnalysis").apply { start() }
                setCallbackHandler(Handler(analyzerThread.looper))
                setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                setTargetRotation(textureView.display.rotation)
            }
            .build()
        val analyzerUseCase = ImageAnalysis(analyzerConfig)
            .apply {
                analyzer = FaceImageAnalyzer(detector, isMirror = lensFacing.isFront())
/*
                analyzer = object : ImageAnalysis.Analyzer {

                    private val ImageProxy.data: ByteArray
                        get() {
                            val y = planes[0]
                            val u = planes[1]
                            val v = planes[2]
                            val Yb = y.buffer.remaining()
                            val Ub = u.buffer.remaining()
                            val Vb = v.buffer.remaining()
                            return ByteArray(Yb + Ub + Vb).apply {
                                y.buffer.get(this, 0, Yb)
                                u.buffer.get(this, Yb, Ub)
                                v.buffer.get(this, Yb + Ub, Vb)
                            }
                        }

                    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
                        binding.gpuImageView.setRotation(Rotation.ROTATION_90)
                        binding.gpuImageView.updatePreviewFrame(
                            image.data,
                            image.width,
                            image.height
                        )
                    }
                }
*/
            }
        CameraX.bindToLifecycle(viewLifecycleOwner, previewUseCase, analyzerUseCase)
    }

    private fun TextureView.updateTransform() {
        val matrix = Matrix()
        val centerX = width / 2f
        val centerY = height / 2f
        val rotationDegrees = when (display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        setTransform(matrix)
    }

    private fun LensFacing.isFront(): Boolean {
        return this == LensFacing.FRONT
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val context: Context = binding.root.context
            if (allPermissionsGranted(context)) {
                binding.root.post {
                    startCameraWith(binding)
                }
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
