package soup.nolan.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import soup.nolan.databinding.EditFragmentBinding
import soup.nolan.ui.base.BaseFragment

class EditFragment : BaseFragment() {

    private val viewModel: EditViewModel by viewModel()

    private lateinit var binding: EditFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initViewState(binding)
        return binding.root
    }

    private fun initViewState(binding: EditFragmentBinding) {
    }
}
