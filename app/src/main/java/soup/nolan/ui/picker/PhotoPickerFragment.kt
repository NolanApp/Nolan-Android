package soup.nolan.ui.picker

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import soup.nolan.R
import soup.nolan.databinding.PhotoPickerBinding
import soup.nolan.ui.purchase.PurchaseViewModel
import soup.nolan.ui.utils.GridSpaceItemDecoration
import soup.nolan.ui.utils.autoCleared

class PhotoPickerFragment : Fragment(R.layout.photo_picker) {

    private var binding: PhotoPickerBinding by autoCleared { adView.destroy() }

    private val viewModel: PhotoPickerViewModel by viewModels()
    private val purchaseViewModel: PurchaseViewModel by activityViewModels()

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                finishResult(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PhotoPickerBinding.bind(view).apply {
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_select_outside -> {
                        galleryLauncher.launch("image/*")
                        true
                    }
                    else -> false
                }
            }

            listView.layoutManager = GridLayoutManager(view.context, 3)
            listView.addItemDecoration(GridSpaceItemDecoration(3, 8))

            val listAdapter = PhotoPickerListAdapter {
                finishResult(it.uri)
            }
            listView.adapter = listAdapter
            viewModel.uiModel.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
            })

            fastScroller.recyclerView = listView

            purchaseViewModel.noAdsPurchased.observe(viewLifecycleOwner, Observer {
                adView.isGone = it
                if (it.not()) {
                    adView.loadAd(AdRequest.Builder().build())
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    private fun finishResult(uri: Uri) {
        setFragmentResult(KEY_REQUEST, bundleOf(EXTRA_FILE_URI to uri))
        findNavController().navigateUp()
    }

    companion object {
        const val KEY_REQUEST = "request_photo_picker"
        const val EXTRA_FILE_URI = "fileUri"
    }
}
