package soup.nolan.ui.widget

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import soup.nolan.R
import soup.nolan.ui.utils.Interpolators

class ContentLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private var mStartTime: Long = -1
    private var mPostedHide = false
    private var mPostedShow = false
    private var mDismissed = false

    private val mDelayedHide = Runnable {
        mPostedHide = false
        mStartTime = -1
        animateVisible(false)
        outerAnimator.cancel()
    }

    private val mDelayedShow = Runnable {
        mPostedShow = false
        if (!mDismissed) {
            mStartTime = System.currentTimeMillis()
            animateVisible(true)
            outerAnimator.start()
        }
    }

    private val outerView: View
    private val outerAnimator: ValueAnimator

    init {
        inflate(getContext(), R.layout.view_content_loading, this)
        outerView = findViewById(R.id.outer)
        outerAnimator = ValueAnimator.ofFloat(1f, 1.05f, 1f, 0.95f, 1f).apply {
            duration = 1000
            interpolator = Interpolators.LINEAR
            repeatMode = RESTART
            repeatCount = INFINITE

            addUpdateListener {
                outerView.scaleX = it.animatedValue as Float
                outerView.scaleY = it.animatedValue as Float
            }
        }
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        removeCallbacks()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (visibility == View.VISIBLE) {
            // Loading View가 보여지는 동안, 모든 Touch Event를 소비한다.
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun removeCallbacks() {
        removeCallbacks(mDelayedHide)
        removeCallbacks(mDelayedShow)
    }

    /**
     * Hide the progress view if it is visible. The progress view will not be
     * hidden until it has been shown for at least a minimum show time. If the
     * progress view was not yet visible, cancels showing the progress view.
     */
    @Synchronized
    fun hide() {
        mDismissed = true
        removeCallbacks(mDelayedShow)
        mPostedShow = false
        val diff = System.currentTimeMillis() - mStartTime
        if (diff >= MIN_SHOW_TIME || mStartTime == -1L) {
            // The progress spinner has been shown long enough
            // OR was not shown yet. If it wasn't shown yet,
            // it will just never be shown.
            animateVisible(false)
        } else {
            // The progress spinner is shown, but not long enough,
            // so put a delayed message in to hide it when its been
            // shown long enough.
            if (!mPostedHide) {
                postDelayed(mDelayedHide, MIN_SHOW_TIME - diff)
                mPostedHide = true
            }
        }
    }

    /**
     * Show the progress view after waiting for a minimum delay. If
     * during that time, hide() is called, the view is never made visible.
     */
    @Synchronized
    fun show() {
        // Reset the start time.
        mStartTime = -1
        mDismissed = false
        removeCallbacks(mDelayedHide)
        mPostedHide = false
        if (!mPostedShow) {
            post(mDelayedShow)
            mPostedShow = true
        }
    }

    private fun View.animateVisible(visible: Boolean) {
        animate().cancel()
        if (visible) {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(200)
                .setInterpolator(Interpolators.LINEAR)
                .withLayer()
                .withEndAction(null)
        } else {
            animate()
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(Interpolators.LINEAR)
                .withLayer()
                .withEndAction {
                    visibility = View.GONE
                }
        }
    }

    companion object {
        private const val MIN_SHOW_TIME = 500 // ms
        private const val MIN_DELAY = 0 // ms
    }
}
