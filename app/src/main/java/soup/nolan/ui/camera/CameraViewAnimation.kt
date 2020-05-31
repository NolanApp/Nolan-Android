package soup.nolan.ui.camera

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.animation.doOnEnd
import soup.nolan.ui.utils.Interpolators
import kotlin.math.hypot

interface CameraViewAnimation {

    fun View.animateShutterFlash() {
        animate().cancel()
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setInterpolator(Interpolators.EASE_OUT_QUINT)
            .setDuration(250)
            .withLayer()
            .withEndAction {
                visibility = View.GONE
            }
    }

    fun View.animateCameraFilterDescription() {
        animate().cancel()
        alpha = 0f
        translationY = 40f
        animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .setInterpolator(Interpolators.EASE_OUT_QUINT)
            .withLayer()
            .withEndAction {
                animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(Interpolators.EASE_OUT_CUBIC)
                    .withLayer()
                    .withEndAction(null)
            }
    }

    fun View.animateCameraFilterDim(target: View) {
        animate().cancel()
        visibility = View.VISIBLE
        alpha = 1f
        createCircularRevealOf(target) {
            duration = 300
            interpolator = Interpolators.FAST_OUT_LINEAR_IN
            doOnEnd {
                animate()
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(Interpolators.EASE_OUT_CUBIC)
                    .withLayer()
                    .withEndAction(null)
            }
        }.start()
    }

    private fun View.createCircularRevealOf(target: View, block: Animator.() -> Unit): Animator {
        return ViewAnimationUtils
            .createCircularReveal(this, (target.right + target.left) / 2, 0, 0f, diagonalLength())
            .apply(block)
    }

    private fun View.diagonalLength(): Float {
        return hypot(width.toFloat(), height.toFloat())
    }
}
