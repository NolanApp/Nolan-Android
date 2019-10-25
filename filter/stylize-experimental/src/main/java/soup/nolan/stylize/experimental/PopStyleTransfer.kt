package soup.nolan.stylize.experimental

import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PopStyleTransfer {

    companion object {

        private const val STYLE_NAME = "pop_art_styler"

        private const val IMAGE_SIZE = 512
        private const val FLOAT_TYPE_SIZE = 4
        private const val PIXEL_SIZE = 3
    }

    private val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)

    private val transformModel by lazy {
        val options = FirebaseModelOptions.Builder()
            .setLocalModelName(STYLE_NAME)
            .build()
        FirebaseModelInterpreter.getInstance(options)!!
    }

    private val transformOptions = FirebaseModelDataType.FLOAT32.let { dataType ->
        FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, PIXEL_SIZE))
            .setOutputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, PIXEL_SIZE))
            .build()
    }

    init {
        val transform = FirebaseLocalModel.Builder(STYLE_NAME)
            .setAssetFilePath("pop_art_styler.tflite")
            .build()
        FirebaseModelManager.getInstance().registerLocalModel(transform)
    }

    fun transform(bitmap: Bitmap): Task<Bitmap> {
        val inputs = FirebaseModelInputs.Builder()
            .add(bitmap.toByteBuffer())
            .build()
        return transformModel.run(inputs, transformOptions)
            .continueWith {
                toBitmap(it.result!!.getOutput(0))
            }
    }

    /**
     * Writes Image data into a `ByteBuffer`.
     */
    @Synchronized
    private fun Bitmap.toByteBuffer(): ByteBuffer {
        val pixels = centerCropped(IMAGE_SIZE).toPixels()
        val inputSize = FLOAT_TYPE_SIZE * IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE
        return ByteBuffer.allocateDirect(inputSize).apply {
            order(ByteOrder.nativeOrder())
            rewind()

            for (pixel in pixels) {
                putFloat((pixel and 0xFF).toFloat())        // B
                putFloat((pixel shr 8 and 0xFF).toFloat())  // G
                putFloat((pixel shr 16 and 0xFF).toFloat()) // R
            }
        }
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

    private fun toBitmap(output: Array<Array<Array<FloatArray>>>): Bitmap {
        var index = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                intValues[index++] = (-0x1000000 or
                        output[0][i][j][0].toInt().shl(16) or
                        output[0][i][j][1].toInt().shl(8) or
                        output[0][i][j][2].toInt())
            }
        }
        //val i = 0
        //val j = 0
        //val R = output[0][i][j][0].toInt()
        //val G = output[0][i][j][1].toInt()
        //val B = output[0][i][j][2].toInt()
        //timber.log.Timber.d("$i, $j = $R $G $B")
        return Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888).apply {
            setPixels(intValues, 0, width, 0, 0, width, height)
        }
    }
}
