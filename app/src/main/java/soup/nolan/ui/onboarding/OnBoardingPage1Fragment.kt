package soup.nolan.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import soup.nolan.R
import soup.nolan.databinding.OnBoardingPage1Binding

class OnBoardingPage1Fragment : Fragment(R.layout.on_boarding_page1) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(OnBoardingPage1Binding.bind(view)) {
            initViewState(this)
        }
    }

    private fun initViewState(binding: OnBoardingPage1Binding) {
    }
}
