package soup.nolan.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = ITEM_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingPageFragment()
            1 -> OnBoardingPageFragment()
            2 -> OnBoardingPageFragment()
            else -> throw IllegalStateException("position is invalid($position)")
        }
    }

    companion object {
        const val ITEM_COUNT = 3
    }
}
