package soup.nolan.ui.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import soup.nolan.R
import soup.nolan.databinding.FilterEditorOptionDialogBinding
import soup.nolan.ui.base.BaseDialogFragment

class FilterEditorOptionDialogFragment : BaseDialogFragment(R.layout.filter_editor_option_dialog) {

    private val viewModel: FilterEditorViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FilterEditorOptionDialogBinding.bind(view).apply {
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
