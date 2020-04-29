package soup.nolan.detect.face.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import soup.nolan.detect.face.model.Face
import kotlin.math.hypot

class FaceGraphic(
    overlay: FaceBlurView,
    private val face: Face
) : FaceBlurView.Graphic(overlay) {

    private val facePositionPaint: Paint = Paint().apply {
        color = Color.WHITE
    }

    private val boxPaint: Paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = BOX_STROKE_WIDTH
    }

    override fun draw(canvas: Canvas) {
        val centerX = translateX(face.boundingBox.centerX().toFloat())
        val centerY = translateY(face.boundingBox.centerY().toFloat())
        canvas.drawCircle(centerX, centerY, FACE_POSITION_RADIUS, facePositionPaint)

        // Draws a bounding box around the face.
        val xOffset = scaleX(face.boundingBox.width() / 2f)
        val yOffset = scaleY(face.boundingBox.height() / 2f)
        val left = centerX - xOffset
        val top = centerY - yOffset
        val right = centerX + xOffset
        val bottom = centerY + yOffset
        canvas.drawRect(left, top, right, bottom, boxPaint)

        val radius = hypot(xOffset, yOffset)
        canvas.drawCircle(centerX, centerY, radius, boxPaint)
    }

    companion object {

        private const val FACE_POSITION_RADIUS = 4f
        private const val BOX_STROKE_WIDTH = 5f
    }
}
