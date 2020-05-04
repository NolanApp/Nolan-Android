package soup.nolan.ui.widget

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import soup.nolan.R

class LensFacingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val facingFrontButton: View
    private val facingBackButton: View

    private var lensFacingFront: Boolean = true

    init {
        inflate(context, R.layout.view_lens_facing, this)
        facingFrontButton = findViewById(R.id.facing_front_button)
        facingBackButton = findViewById(R.id.facing_back_button)
    }

    fun setLensFacing(front: Boolean) {
        lensFacingFront = front
        val flipOut = AnimatorInflater.loadAnimator(context, R.animator.flip_out)
        val flipIn = AnimatorInflater.loadAnimator(context, R.animator.flip_in)
        if (front) {
            flipOut.setTarget(facingBackButton)
            flipIn.setTarget(facingFrontButton)
            flipOut.start()
            flipIn.start()
        } else {
            flipOut.setTarget(facingFrontButton)
            flipIn.setTarget(facingBackButton)
            flipOut.start()
            flipIn.start()
        }
    }

    fun isLensFacingFront(): Boolean {
        return lensFacingFront
    }
}
