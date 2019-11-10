package soup.nolan.stylize.popart

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.custom.*
import soup.nolan.stylize.common.centerCropped
import soup.nolan.stylize.common.toPixels
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

    private val transformModel: FirebaseModelInterpreter by lazy {
        val localModel = FirebaseCustomLocalModel.Builder()
            .setAssetFilePath("pop_art_styler.tflite")
            .build()
        val options = FirebaseModelInterpreterOptions.Builder(localModel).build()
        FirebaseModelInterpreter.getInstance(options)!!
    }

    private val transformOptions = FirebaseModelDataType.FLOAT32.let { dataType ->
        FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, PIXEL_SIZE))
            .setOutputFormat(0, dataType, intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, PIXEL_SIZE))
            .build()
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
        val pixels = centerCropped(IMAGE_SIZE).toPixels(intValues)
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
        return Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888).apply {
            setPixels(intValues, 0, width, 0, 0, width, height)
        }
    }
}
