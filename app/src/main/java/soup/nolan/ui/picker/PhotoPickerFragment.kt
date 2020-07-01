package soup.nolan.ui.picker

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import soup.nolan.R
import soup.nolan.databinding.PhotoPickerBinding
import soup.nolan.ui.utils.GridSpaceItemDecoration

class PhotoPickerFragment : Fragment(R.layout.photo_picker) {

    private val viewModel: PhotoPickerViewModel by viewModels()

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                finishResult(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(PhotoPickerBinding.bind(view)) {
            //TODO: connect to galleryLauncher
            //galleryLauncher.launch("image/*")

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
        }
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
