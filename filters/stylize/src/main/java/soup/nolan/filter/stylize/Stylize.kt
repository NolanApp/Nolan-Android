package soup.nolan.filter.stylize

import android.content.res.AssetManager
import android.graphics.Bitmap

import org.tensorflow.contrib.android.TensorFlowInferenceInterface

internal class Stylize(assetManager: AssetManager) {

    private val inference = TensorFlowInferenceInterface(assetManager, MODEL_FILE)
    private val styleValues = ArrayList<Float>()

    fun setStyleValues(styleValues: FloatArray) {
        this.styleValues.clear()
        this.styleValues.addAll(styleValues.toList())
    }

    fun stylize(bitmap: Bitmap, desiredSize: Int): Bitmap {
        val intValues = IntArray(desiredSize * desiredSize)
        val floatValues = FloatArray(desiredSize * desiredSize * 3)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in intValues.indices) {
            val value = intValues[i]
            floatValues[i * 3] = (value shr 16 and 0xFF) / 255f
            floatValues[i * 3 + 1] = (value shr 8 and 0xFF) / 255f
            floatValues[i * 3 + 2] = (value and 0xFF) / 255f
        }

        // Copy the input data into TensorFlow.
        inference.feed(INPUT_NODE, floatValues, 1L, bitmap.width.toLong(), bitmap.height.toLong(), 3L)
        inference.feed(STYLE_NODE, styleValues.toFloatArray(), styleValues.size.toLong())

        // Execute the output node's dependency sub-graph.
        inference.run(arrayOf(OUTPUT_NODE), false)

        // Copy the data from TensorFlow back into our array.
        inference.fetch(OUTPUT_NODE, floatValues)

        for (i in intValues.indices) {
            intValues[i] = (-0x1000000
                    or (floatValues[i * 3] * 255).toInt().shl(16)
                    or (floatValues[i * 3 + 1] * 255).toInt().shl(8)
                    or (floatValues[i * 3 + 2] * 255).toInt())
        }

        bitmap.setPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return Bitmap.createBitmap(bitmap)
    }

    companion object {
        private const val MODEL_FILE = "file:///android_asset/stylize_quantized.pb"
        private const val INPUT_NODE = "input"
        private const val STYLE_NODE = "style_num"
        private const val OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid"
    }
}
