package soup.nolan.ui.camera.filter.editor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import soup.nolan.R
import soup.nolan.databinding.FilterEditorOptionDialogBinding
import soup.nolan.ui.base.BaseDialogFragment

@AndroidEntryPoint
class FilterEditorOptionDialogFragment : BaseDialogFragment(R.layout.filter_editor_option_dialog) {

    private val viewModel: FilterEditorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FilterEditorOptionDialogBinding.bind(view).apply {
            defaultButton.setOnClickListener {
                viewModel.onDefaultClick()
                dismiss()
            }
            cameraButton.setOnClickListener {
                viewModel.onCameraClick()
                dismiss()
            }
            albumButton.setOnClickListener {
                viewModel.onAlbumClick()
                dismiss()
            }
        }
    }
}
