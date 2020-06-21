package soup.nolan.ui.edit

import android.view.View
import soup.nolan.ui.utils.Interpolators

interface PhotoEditViewAnimation {

    fun View.animateIn() {
        animate().cancel()
        alpha = 0f
        translationY = 40f
        visibility = View.VISIBLE
        animate()
            .setStartDelay(500)
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .setInterpolator(Interpolators.EASE_OUT_QUINT)
            .withLayer()
            .withEndAction(null)
    }

    fun View.animateOut(endAction: () -> Unit = {}) {
        animate().cancel()
        animate()
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(Interpolators.EASE_OUT_CUBIC)
            .withLayer()
            .withEndAction {
                visibility = View.GONE
                endAction()
            }
    }

    fun View.animateVisible(visible: Boolean) {
        animate().cancel()
        if (visible) {
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(Interpolators.EASE_OUT_QUINT)
                .withLayer()
                .withEndAction(null)
        } else {
            animate()
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(Interpolators.EASE_OUT_CUBIC)
                .withLayer()
                .withEndAction {
                    visibility = View.GONE
                }
        }
    }
}
