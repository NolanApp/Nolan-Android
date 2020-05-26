package soup.nolan.ui.splash

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import soup.nolan.R
import soup.nolan.databinding.SplashBinding
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToCamera

class SplashFragment : Fragment(R.layout.splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SplashBinding.bind(view)) {
            logo.run {
                alpha = 0f
                scaleX = 0f
                scaleY = 0f
                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(250)
                    .setInterpolator(OvershootInterpolator())
                    .setListener(object : Animator.AnimatorListener {

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                        override fun onAnimationStart(animation: Animator) {}

                        override fun onAnimationEnd(animation: Animator) {
                            findNavController().navigate(actionToCamera())
                        }
                    })
            }
        }
    }
}
