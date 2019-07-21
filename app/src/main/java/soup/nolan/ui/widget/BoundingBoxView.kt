package soup.nolan.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Size
import android.view.View
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

class BoundingBoxView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var scaleFactor: Float = 1f

    private val boundingBoxList = ArrayList<Rect>()

    private val strokePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.run {
            boundingBoxList.forEach {
                drawRect(
                    it.left * scaleFactor,
                    it.top * scaleFactor,
                    it.right * scaleFactor,
                    it.bottom * scaleFactor,
                    strokePaint
                )
            }
        }
    }

    fun setBoundingBoxFrame(size: Size) {
        scaleFactor = min(width.toFloat() / size.width, height.toFloat() / size.height)
        Timber.d("scaleFactor=$scaleFactor")
        postInvalidate()
    }

    fun setBoundingBoxList(list: List<Rect>) {
        boundingBoxList.clear()
        boundingBoxList.addAll(list)
        postInvalidate()
    }
}
