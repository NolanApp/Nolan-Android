package soup.nolan.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import soup.nolan.databinding.SettingsFragmentBinding
import soup.nolan.ui.base.BaseFragment

class SettingsFragment : BaseFragment() {

    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}
