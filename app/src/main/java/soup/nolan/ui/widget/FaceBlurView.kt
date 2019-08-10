package soup.nolan.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView
import soup.nolan.R
import soup.nolan.model.Face
import soup.nolan.ui.utils.blur
import soup.nolan.ui.utils.erase

class FaceBlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val showDebugBound: Boolean

    private val lock = Any()
    private val graphics = arrayListOf<Graphic>()
    private var previewWidth: Int = 0
    private var widthScaleFactor: Float = 1f
    private var previewHeight: Int = 0
    private var heightScaleFactor: Float = 1f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.FaceBlurView, defStyleAttr, 0)
        try {
            showDebugBound = a.getBoolean(R.styleable.FaceBlurView_showDebugBound, false)
        } finally {
            a.recycle()
        }
        scaleType = ScaleType.CENTER_CROP
    }

    abstract class Graphic(private val overlay: FaceBlurView) {

        abstract fun draw(canvas: Canvas)

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view scale.
         */
        fun scaleX(horizontal: Float): Float {
            return horizontal * overlay.widthScaleFactor
        }

        /** Adjusts a vertical value of the supplied value from the preview scale to the view scale.  */
        fun scaleY(vertical: Float): Float {
            return vertical * overlay.heightScaleFactor
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate system.
         */
        fun translateX(x: Float): Float {
            return scaleX(x)
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate system.
         */
        fun translateY(y: Float): Float {
            return scaleY(y)
        }
    }

    /** Removes all graphics from the overlay.  */
    fun clear() {
        synchronized(lock) {
            graphics.clear()
        }
        postInvalidate()
    }

    fun renderFaceList(originalImage: Bitmap, faceList: List<Face>) {
        if (faceList.isEmpty()) {
            setImageBitmap(null)
        } else {
            setImageBitmap(
                originalImage
                    .erase(faceList.map { it.boundingBox })
                    .blur()
            )
        }
        graphics.clear()
        faceList.forEach {
            graphics.add(FaceGraphic(this, it))
        }
        postInvalidate()
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform image
     * coordinates later.
     */
    fun setCameraInfo(previewWidth: Int, previewHeight: Int) {
        synchronized(lock) {
            this.previewWidth = previewWidth
            this.previewHeight = previewHeight
        }
        postInvalidate()
    }

    /** Draws the overlay with its associated graphic objects.  */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showDebugBound) {
            synchronized(lock) {
                if (previewWidth != 0 && previewHeight != 0) {
                    widthScaleFactor = width.toFloat() / previewWidth.toFloat()
                    heightScaleFactor = height.toFloat() / previewHeight.toFloat()
                }

                for (graphic in graphics) {
                    graphic.draw(canvas)
                }
            }
        }
    }
}
