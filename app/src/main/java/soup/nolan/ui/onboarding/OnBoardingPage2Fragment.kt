package soup.nolan.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import soup.nolan.R
import soup.nolan.databinding.OnBoardingPage2Binding

class OnBoardingPage2Fragment : Fragment(R.layout.on_boarding_page2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(OnBoardingPage2Binding.bind(view)) {
            initViewState(this)
        }
    }

    private fun initViewState(binding: OnBoardingPage2Binding) {
    }
}
