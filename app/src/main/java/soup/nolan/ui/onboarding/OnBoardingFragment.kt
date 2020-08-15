package soup.nolan.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import soup.nolan.R
import soup.nolan.databinding.OnBoardingBinding
import soup.nolan.ui.utils.Interpolators
import soup.nolan.ui.utils.autoCleared
import soup.nolan.ui.utils.setOnDebounceClickListener

class OnBoardingFragment : Fragment(R.layout.on_boarding) {

    private var binding: OnBoardingBinding by autoCleared()
    private val viewModel: OnBoardingViewModel by activityViewModels()

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            binding.onPageSelected(position)
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.viewPager.previousPage().not()) {
                activity?.finish()
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

    override fun onResume() {
        super.onResume()
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onPause() {
        super.onPause()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    private fun initViewState(binding: OnBoardingBinding) {
        binding.viewPager.adapter = OnBoardingPagerAdapter(this)

        binding.nextButton.setOnClickListener {
            binding.viewPager.nextPage()
        }
        binding.startButton.setOnDebounceClickListener {
            viewModel.onClickStart()
        }
        binding.onPageSelected(binding.viewPager.currentItem)
    }

    private fun OnBoardingBinding.onPageSelected(position: Int) {
        pagerDescription.run {
            when (position) {
                0 -> setText(R.string.onboarding_page1_desc)
                1 -> setText(R.string.onboarding_page2_desc)
                2 -> setText(R.string.onboarding_page3_desc)
                else -> Unit
            }
            animateSlideUp()
        }

        val isLastPage = viewPager.currentItem == OnBoardingPagerAdapter.ITEM_COUNT - 1
        nextButton.isGone = isLastPage
        startButton.isVisible = isLastPage
    }

    private fun ViewPager2.previousPage(): Boolean {
        val last = currentItem
        currentItem -= 1
        return last != currentItem
    }

    private fun ViewPager2.nextPage(): Boolean {
        val last = currentItem
        currentItem += 1
        return last != currentItem
    }

    private fun View.animateSlideUp() {
        animate().cancel()
        alpha = 0f
        translationY = 40f
        animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(Interpolators.EASE_OUT_QUINT)
            .withLayer()
            .withEndAction(null)
    }
}
