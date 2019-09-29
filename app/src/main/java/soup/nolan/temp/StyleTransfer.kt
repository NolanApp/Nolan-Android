package soup.nolan.temp

import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

object StyleTransfer {

    private const val STYLE_PREDICT = "style_predict"
    private const val STYLE_TRANSFORM = "style_transform"

    private const val IMAGE_SIZE = 512
    private const val FLOAT_NUM_OF_BYTES_PER_CHANNEL = 4

    private val predictModel by lazy {
        val options = FirebaseModelOptions.Builder()
                .setLocalModelName(STYLE_PREDICT)
                .build()
        FirebaseModelInterpreter.getInstance(options)!!
    }

    private val predictOptions = FirebaseModelDataType.FLOAT32.let { dataType ->
        FirebaseModelInputOutputOptions.Builder()
                .setInputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, 3))
                .setOutputFormat(0, dataType, intArrayOf(1, 1, 1, 100))
                .build()
    }

    private val transformModel by lazy {
        val options = FirebaseModelOptions.Builder()
                .setLocalModelName(STYLE_TRANSFORM)
                .build()
        FirebaseModelInterpreter.getInstance(options)!!
    }

    private val transformOptions = FirebaseModelDataType.FLOAT32.let { dataType ->
        FirebaseModelInputOutputOptions.Builder()
                .setInputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, 3))
                .setInputFormat(1, dataType, intArrayOf(1, 1, 1, 100))
                .setOutputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, 3))
                .build()
    }

    fun init() {
        val predict = FirebaseLocalModel.Builder(STYLE_PREDICT)
                .setAssetFilePath("style_predict-512.tflite")
                .build()
        FirebaseModelManager.getInstance().registerLocalModel(predict)

        val transform = FirebaseLocalModel.Builder(STYLE_TRANSFORM)
                .setAssetFilePath("style_transform-512.tflite")
                .build()
        FirebaseModelManager.getInstance().registerLocalModel(transform)
    }

    fun predict(bitmap: Bitmap): Task<Array<Array<Array<FloatArray>>>> {
        val imgData = bitmap.toByteBuffer()
        val inputs = FirebaseModelInputs.Builder().add(imgData).build()
        return predictModel.run(inputs, predictOptions)
                .continueWith {
                    it.result!!.getOutput<Array<Array<Array<FloatArray>>>>(0)
                }
    }

    fun transform(style: Bitmap, bitmap: Bitmap): Task<Bitmap> {
        return predict(style)
                .continueWithTask {
                    val inputs = FirebaseModelInputs.Builder()
                            .add(bitmap.toByteBuffer())
                            .add(it.result!!)
                            .build()
                    transformModel.run(inputs, transformOptions)
                }
                .continueWith {
                    val result = it.result!!.getOutput<Array<Array<Array<FloatArray>>>>(0)
                    toBitmap(result)
                }
    }

    private fun toBitmap(result: Array<Array<Array<FloatArray>>>): Bitmap {
        var index = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                intValues[index++] = (-0x1000000 or
                        (result[0][i][j][0] * 255).toInt().shl(16) or
                        (result[0][i][j][1] * 255).toInt().shl(8) or
                        (result[0][i][j][2] * 255).toInt())
            }
        }
        return Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888).apply {
            setPixels(intValues, 0, width, 0, 0, width, height)
        }
    }

    private val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
    /**
     * Writes Image data into a `ByteBuffer`.
     */
    @Synchronized
    private fun Bitmap.toByteBuffer(): ByteBuffer {
        val pixels = centerCropped(IMAGE_SIZE).toPixels()
        val imgData = ByteBuffer.allocateDirect(FLOAT_NUM_OF_BYTES_PER_CHANNEL * IMAGE_SIZE * IMAGE_SIZE * 3)
        imgData.order(ByteOrder.nativeOrder())
        imgData.rewind()
        // Convert the image to int points.
        var index = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                val value = pixels[index++]
                imgData.putFloat((value shr 16 and 0xFF) / 255f)
                imgData.putFloat((value shr 8 and 0xFF) / 255f)
                imgData.putFloat((value and 0xFF) / 255f)
            }
        }
        return imgData
    }

    private fun Bitmap.centerCropped(size: Int): Bitmap {
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also {
            val frameToCropTransform = ImageUtils.getTransformationMatrix(
                    width, height,
                    size, size,
                    0, true
            )
            Canvas(it).drawBitmap(this, frameToCropTransform, null)
        }
    }

    private fun Bitmap.toPixels(): IntArray {
        getPixels(intValues, 0, width, 0, 0, width, height)
        return intValues
    }
}
