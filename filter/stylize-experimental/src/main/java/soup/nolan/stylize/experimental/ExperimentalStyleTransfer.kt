package soup.nolan.stylize.experimental

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
import soup.nolan.stylize.common.centerCropped
import soup.nolan.stylize.common.toPixels
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.random.Random

class ExperimentalStyleTransfer {

    private val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)

    private val predictModel by lazy {
        val options = FirebaseModelOptions.Builder()
                .setLocalModelName(STYLE_PREDICT)
                .build()
        FirebaseModelInterpreter.getInstance(options)!!
    }

    private val predictOptions = FirebaseModelDataType.FLOAT32.let { dataType ->
        FirebaseModelInputOutputOptions.Builder()
                .setInputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, PIXEL_SIZE))
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
                .setInputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, PIXEL_SIZE))
                .setInputFormat(1, dataType, intArrayOf(1, 1, 1, 100))
                .setOutputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, PIXEL_SIZE))
                .build()
    }

    init {
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
//                            .apply { Timber.d("predict=${first().first().first().toList()}") }
                }
    }

    fun transform(style: Bitmap, bitmap: Bitmap): Task<Bitmap> {
//        return predict(style)
//                .continueWithTask {
//                    val inputs = FirebaseModelInputs.Builder()
//                        .add(bitmap.toByteBuffer())
//                        .add(it.result!!)
//                        .build()
//                    transformModel.run(inputs, transformOptions)
//                }
//                .continueWith {
//                    toBitmap(it.result!!.getOutput(0))
//                }
        val inputs = FirebaseModelInputs.Builder()
            .add(bitmap.toByteBuffer())
            .add(randomStyle())
            .build()
        return transformModel.run(inputs, transformOptions)
            .continueWith {
                toBitmap(it.result!!.getOutput(0))
            }
    }

    private fun randomStyle(): Array<Array<Array<FloatArray>>> {
        return arrayOf(arrayOf(arrayOf(FloatArray(100) {
            Random.nextDouble(-10.0, 10.0).toFloat()
        })))
    }

    /**
     * Writes Image data into a `ByteBuffer`.
     */
    @Synchronized
    private fun Bitmap.toByteBuffer(): ByteBuffer {
        val pixels = centerCropped(IMAGE_SIZE).toPixels(intValues)
        val inputSize = FLOAT_TYPE_SIZE * IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE
        return ByteBuffer.allocateDirect(inputSize).apply {
            order(ByteOrder.nativeOrder())
            rewind()

            for (pixel in pixels) {
                putFloat((pixel shr 16 and 0xFF).toFloat()) // R
                putFloat((pixel shr 8 and 0xFF).toFloat())  // G
                putFloat((pixel and 0xFF).toFloat())        // B
//                Timber.d("$pixel =" +
//                        " ${(pixel shr 16 and 0xFF)}" +
//                        " ${(pixel shr 8 and 0xFF)}" +
//                        " ${(pixel and 0xFF)}")
            }
        }
    }

    private fun toBitmap(output: Array<Array<Array<FloatArray>>>): Bitmap {
        var index = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                intValues[index++] = (-0x1000000 or
                        ((output[0][i][j][0] + IMAGE_OFFSET) * IMAGE_MEAN).toInt().shl(16) or
                        ((output[0][i][j][1] + IMAGE_OFFSET) * IMAGE_MEAN).toInt().shl(8) or
                        ((output[0][i][j][2] + IMAGE_OFFSET) * IMAGE_MEAN).toInt())
            }
        }
        //val i = 0
        //val j = 0
        //val R = ((output[0][i][j][0] + IMAGE_OFFSET) * IMAGE_MEAN).toInt()
        //val G = ((output[0][i][j][1] + IMAGE_OFFSET) * IMAGE_MEAN).toInt()
        //val B = ((output[0][i][j][2] + IMAGE_OFFSET) * IMAGE_MEAN).toInt()
        //timber.log.Timber.d("$i, $j = $R $G $B")
        return Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888).apply {
            setPixels(intValues, 0, width, 0, 0, width, height)
        }
    }

    companion object {

        private const val STYLE_PREDICT = "style_predict"
        private const val STYLE_TRANSFORM = "style_transform"

        private const val IMAGE_SIZE = 512
        private const val IMAGE_MEAN = 128f
        private const val IMAGE_OFFSET = 1f
        private const val FLOAT_TYPE_SIZE = 4
        private const val PIXEL_SIZE = 3
    }
}
