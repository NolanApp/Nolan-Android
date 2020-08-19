package soup.nolan.ui.splash

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import soup.nolan.R
import soup.nolan.databinding.SplashBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.permission.PermissionFragment
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToCamera
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToFilterEditor
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToOnBoarding
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToPermission
import soup.nolan.ui.utils.autoCleared

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.splash) {

    private val viewModel: SplashViewModel by viewModels()

    private var binding: SplashBinding by autoCleared()
    private var isAnimating = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SplashBinding.bind(view).apply {
            logoInner.animateBounce()

            viewModel.navigationEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    SplashUiEvent.GoToOnBoarding -> {
                        findNavController().navigate(actionToOnBoarding())
                    }
                    SplashUiEvent.GoToPermission -> {
                        findNavController().navigate(actionToPermission())
                    }
                    SplashUiEvent.GoToFilterEditor -> {
                        findNavController().navigate(actionToFilterEditor())
                    }
                    SplashUiEvent.GoToCamera -> {
                        findNavController().navigate(
                            actionToCamera(),
                            FragmentNavigatorExtras(logoOuter.let { v -> v to v.transitionName })
                        )
                    }
                }
            })

            binding = this
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAnimating) {
            isAnimating = false
            context?.done()
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
                    context.done()
                }
            })
    }

    private fun Context.done() {
        viewModel.onAnimationEnd(PermissionFragment.hasRequiredPermissions(this))
    }
}
