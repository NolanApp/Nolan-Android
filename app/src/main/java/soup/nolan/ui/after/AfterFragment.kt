package soup.nolan.ui.after

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import soup.nolan.databinding.AfterFragmentBinding
import soup.nolan.ui.base.BaseFragment

class AfterFragment : BaseFragment() {

    private val viewModel: AfterViewModel by viewModel()

    private lateinit var binding: AfterFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AfterFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initViewState(binding)
        return binding.root
    }

    private fun initViewState(binding: AfterFragmentBinding) {
    }
}
