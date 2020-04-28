package soup.nolan.ui.share

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import soup.nolan.R
import soup.nolan.databinding.ShareBinding
import soup.nolan.ui.base.BaseFragment

class ShareFragment : BaseFragment(R.layout.share) {

    private val args: ShareFragmentArgs by navArgs()
    private val viewModel: ShareViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(ShareBinding.bind(view)) {
            //TODO:
            //viewModel.onShareClick()
        }
    }
}
