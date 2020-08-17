package soup.nolan.ui.splash

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import soup.nolan.Dependency
import soup.nolan.R
import soup.nolan.databinding.SplashBinding
import soup.nolan.ui.permission.PermissionFragment
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToCamera
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToOnBoarding
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToPermission
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToFilterEditor
import soup.nolan.ui.utils.autoCleared

class SplashFragment : Fragment(R.layout.splash) {

    private var binding: SplashBinding by autoCleared()
    private var isAnimating = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SplashBinding.bind(view)) {
            logoInner.animateBounce()
            binding = this
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAnimating) {
            isAnimating = false
            navigateToCamera()
        }
    }

    private fun View.animateBounce() {
        alpha = 0f
        scaleX = 0f
        scaleY = 0f
        animate()
            .setStartDelay(200)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(350)
            .setInterpolator(OvershootInterpolator(2.5f))
            .withLayer()
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationStart(animation: Animator) {
                    isAnimating = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    when {
                        Dependency.appSettings.showOnBoarding -> navigateToOnBoarding()
                        PermissionFragment.showPermission(context) -> navigateToPermission()
                        Dependency.appSettings.showFilterEditor -> navigateToFilterEditor()
                        else -> navigateToCamera()
                    }
                }
            })
    }

    private fun navigateToOnBoarding() {
        findNavController().navigate(actionToOnBoarding())
    }

    private fun navigateToPermission() {
        findNavController().navigate(actionToPermission())
    }

    private fun navigateToFilterEditor() {
        findNavController().navigate(actionToFilterEditor())
    }

    private fun navigateToCamera() {
        val extras = FragmentNavigatorExtras(binding.logoOuter.let { it to it.transitionName })
        findNavController().navigate(actionToCamera(), extras)
    }
}
