package soup.nolan.ui.camera.filter.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.databinding.FilterEditorHeaderBinding
import soup.nolan.ui.utils.AlwaysDiffCallback
import soup.nolan.ui.utils.setOnDebounceClickListener

class FilterEditorHeaderAdapter(
    private val onHeaderClick: (FilterEditorHeaderUiModel) -> Unit
) : ListAdapter<FilterEditorHeaderUiModel, FilterEditorHeaderAdapter.ViewHolder>(AlwaysDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FilterEditorHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            getItem(it)?.run(onHeaderClick)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: FilterEditorHeaderBinding,
        private val onHeaderClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.thumbnail.setOnDebounceClickListener {
                onHeaderClick(bindingAdapterPosition)
            }
        }

        fun bind(uiModel: FilterEditorHeaderUiModel) {
            binding.thumbnail.setImageURI(uiModel.imageUri)
        }
    }
}
