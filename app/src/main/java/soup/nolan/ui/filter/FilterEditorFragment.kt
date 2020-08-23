package soup.nolan.ui.filter

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import soup.nolan.R
import soup.nolan.databinding.FilterEditorBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.filter.FilterEditorFragmentDirections.Companion.actionToCamera
import soup.nolan.ui.filter.FilterEditorFragmentDirections.Companion.actionToOption
import soup.nolan.ui.utils.setOnDebounceClickListener

@AndroidEntryPoint
class FilterEditorFragment : Fragment(R.layout.filter_editor) {

    private var lastCameraImageUri: Uri? = null

    private val viewModel: FilterEditorViewModel by activityViewModels()

    private val cameraPicker =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { granted ->
            if (granted) {
                lastCameraImageUri?.let { uri ->
                    viewModel.onOriginImageChanged(uri)
                }
            }
        }

    private val albumPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                viewModel.onOriginImageChanged(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FilterEditorBinding.bind(view).apply {
            val headerAdapter = FilterEditorHeaderAdapter {
                findNavController().navigate(actionToOption())
            }
            val listAdapter = FilterEditorListAdapter {
                viewModel.onItemClick(it)
            }
            listView.layoutManager = GridLayoutManager(view.context, 4).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) 4 else 1
                    }
                }
            }
            listView.itemAnimator = null
            listView.adapter = ConcatAdapter(headerAdapter, listAdapter)

            doneButton.setOnDebounceClickListener {
                viewModel.onStartClick()
            }

            viewModel.header.observe(viewLifecycleOwner, Observer {
                headerAdapter.submitList(listOf(it))
            })
            viewModel.list.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
            })
            viewModel.canDone.observe(viewLifecycleOwner, Observer {
                doneButton.isEnabled = it
            })
            viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    is FilterEditorUiEvent.TakePicture -> {
                        lastCameraImageUri = it.uri
                        cameraPicker.launch(it.uri)
                    }
                    is FilterEditorUiEvent.PickFromAlbum -> {
                        albumPicker.launch("image/*")
                    }
                    is FilterEditorUiEvent.GoToCamera -> {
                        if (parentFragmentManager.backStackEntryCount == 0) {
                            findNavController().navigate(actionToCamera())
                        } else {
                            findNavController().navigateUp()
                        }
                    }
                }
            })
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            lastCameraImageUri = it.getString(KEY_CAMERA_IMAGE_URI)?.toUri()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastCameraImageUri?.let {
            outState.putString(KEY_CAMERA_IMAGE_URI, it.toString())
        }
    }

    companion object {
        private const val KEY_CAMERA_IMAGE_URI = "camera_image_uri"
    }
}
