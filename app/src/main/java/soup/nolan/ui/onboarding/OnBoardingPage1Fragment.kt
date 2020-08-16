package soup.nolan.ui.onboarding

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import soup.nolan.R
import soup.nolan.databinding.OnBoardingPage1Binding

class OnBoardingPage1Fragment : Fragment(R.layout.on_boarding_page1) {

    private val animator = ValueAnimator.ofFloat(-0.5f, 1.5f).apply {
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        duration = 1_600L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(OnBoardingPage1Binding.bind(view)) {
            animator.addUpdateListener {
                val value = 10000 * it.animatedValue as Float
                image.setImageLevel(value.toInt().coerceIn(0, 10000))
            }
        }
        animator.start()
    }

    override fun onDestroyView() {
        animator.cancel()
        animator.removeAllListeners()
        animator.removeAllUpdateListeners()
        super.onDestroyView()
    }
}
