package soup.nolan.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postOnAnimationDelayed
import androidx.navigation.fragment.findNavController
import soup.nolan.databinding.SplashFragmentBinding
import soup.nolan.ui.base.BaseFragment

class SplashFragment : BaseFragment() {

    private lateinit var binding: SplashFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SplashFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.postOnAnimationDelayed(1000) {
            findNavController().navigate(SplashFragmentDirections.actionToCamera())
        }
    }
}
