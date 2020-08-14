package soup.nolan.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import soup.nolan.R
import soup.nolan.databinding.OnBoardingPageBinding

class OnBoardingPageFragment : Fragment(R.layout.on_boarding_page) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(OnBoardingPageBinding.bind(view)) {
            initViewState(this)
        }
    }

    private fun initViewState(binding: OnBoardingPageBinding) {
    }
}
