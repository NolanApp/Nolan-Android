package soup.nolan.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import soup.nolan.databinding.ShareFragmentBinding
import soup.nolan.ui.base.BaseFragment

class ShareFragment : BaseFragment() {

    private val args: ShareFragmentArgs by navArgs()
    private val viewModel: ShareViewModel by viewModel()

    private lateinit var binding: ShareFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShareFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}
