package soup.nolan.ui.filter

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.databinding.FilterEditorBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.filter.FilterEditorFragmentDirections.Companion.actionToCamera
import soup.nolan.ui.filter.FilterEditorFragmentDirections.Companion.actionToOption
import soup.nolan.ui.filter.FilterEditorListAdapter.Companion.VIEW_TYPE_HEADER
import soup.nolan.ui.utils.setOnDebounceClickListener
import java.io.File

@AndroidEntryPoint
class FilterEditorFragment : Fragment(R.layout.filter_editor) {

    private val viewModel: FilterEditorViewModel by activityViewModels()

    private val cameraPicker = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            lastImageUri?.let { uri ->
                viewModel.onOriginImageChanged(uri)
            }
        }
    }

    private val albumPicker = registerForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.onOriginImageChanged(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FilterEditorBinding.bind(view).apply {
            val adapter = FilterEditorListAdapter {
                when (it) {
                    is FilterEditorUiModel.Header -> {
                        findNavController().navigate(actionToOption())
                    }
                    is FilterEditorUiModel.Item -> viewModel.onItemClick(it)
                }
            }
            listView.layoutManager = GridLayoutManager(view.context, 4).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter.getItemViewType(position)) {
                            VIEW_TYPE_HEADER -> 4
                            else -> 1
                        }
                    }
                }
            }
            listView.itemAnimator = null
            listView.adapter = adapter
            viewModel.uiModel.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })

            startButton.setOnDebounceClickListener {
                viewModel.onStartClick()
            }
            viewModel.canStart.observe(viewLifecycleOwner, Observer {
                startButton.isEnabled = it
            })

            viewModel.uiEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    FilterEditorUiEvent.TakePicture -> {
                        lastImageUri = view.context.createImageUri()?.also { uri ->
                            cameraPicker.launch(uri)
                        }
                    }
                    FilterEditorUiEvent.PickFromAlbum -> {
                        albumPicker.launch("image/*")
                    }
                    FilterEditorUiEvent.GoToCamera -> {
                        findNavController().navigate(actionToCamera())
                    }
                }
            })
        }
    }

    private var lastImageUri: Uri? = null

    private fun Context.createImageUri(): Uri? {
        val saveDir = File(filesDir, IMAGE_FILE_PATH)
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }
        return FileProvider.getUriForFile(
            this,
            BuildConfig.FILES_AUTHORITY,
            File(saveDir, IMAGE_FILE_NAME)
        )
    }

    companion object {
        private const val IMAGE_FILE_PATH = "image_manager/photo/"
        private const val IMAGE_FILE_NAME = "camera_image.jpg"
    }
}
