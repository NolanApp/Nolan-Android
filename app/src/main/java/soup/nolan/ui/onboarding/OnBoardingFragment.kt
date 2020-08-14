package soup.nolan.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import soup.nolan.Dependency
import soup.nolan.R
import soup.nolan.databinding.OnBoardingBinding
import soup.nolan.ui.splash.SplashFragmentDirections.Companion.actionToPermission
import soup.nolan.ui.utils.autoCleared
import soup.nolan.ui.utils.setOnDebounceClickListener

class OnBoardingFragment : Fragment(R.layout.on_boarding) {

    private var binding: OnBoardingBinding by autoCleared {
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            val isLastPage = binding.viewPager.currentItem == OnBoardingPagerAdapter.ITEM_COUNT - 1
            binding.nextButton.isGone = isLastPage
            binding.startButton.isVisible = isLastPage
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.viewPager.nextPage().not()) {
                navigateToNext()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(OnBoardingBinding.bind(view)) {
            initViewState(this)
            binding = this
        }
    }

    private fun initViewState(binding: OnBoardingBinding) {
        binding.viewPager.adapter = OnBoardingPagerAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)

        binding.skipButton.setOnClickListener {
            navigateToNext()
        }
        binding.nextButton.setOnClickListener {
            binding.viewPager.nextPage()
        }
        binding.startButton.setOnDebounceClickListener {
            navigateToNext()
        }
    }

    private fun navigateToNext() {
        Dependency.appSettings.showOnBoarding = false
        findNavController().navigate(actionToPermission())
    }

    private fun ViewPager2.nextPage(): Boolean {
        val last = currentItem
        currentItem += 1
        return last != currentItem
    }
}
