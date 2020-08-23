package soup.nolan.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.databinding.FilterEditorItemBinding
import soup.nolan.ui.utils.IdBasedDiffCallback

class FilterEditorListAdapter(
    private val onItemClick: (FilterEditorItemUiModel) -> Unit
) : ListAdapter<FilterEditorItemUiModel, FilterEditorListAdapter.ViewHolder>(IdBasedDiffCallback { it.key }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FilterEditorItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            getItem(it)?.run(onItemClick)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: FilterEditorItemBinding,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(uiModel: FilterEditorItemUiModel) {
            binding.filterSelected.isVisible = uiModel.isSelected
            binding.thumbnail.setImageURI(uiModel.filter.imageUri)
            binding.progressBar.isVisible = uiModel.filter.inProgress
            binding.label.text = uiModel.filter.id
        }
    }
}
