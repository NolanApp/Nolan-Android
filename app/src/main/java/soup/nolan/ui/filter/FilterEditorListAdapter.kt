package soup.nolan.ui.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.databinding.FilterEditorHeaderBinding
import soup.nolan.databinding.FilterEditorItemBinding
import soup.nolan.ui.utils.IdBasedDiffCallback
import soup.nolan.ui.utils.setOnDebounceClickListener

class FilterEditorListAdapter(
    private val onItemClick: (FilterEditorUiModel) -> Unit
) : ListAdapter<FilterEditorUiModel, FilterEditorListAdapter.ViewHolder>(IdBasedDiffCallback { it.key }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(
                FilterEditorHeaderBinding.inflate(layoutInflater, parent, false)
            ) {
                getItem(it)?.run(onItemClick)
            }
        } else {
            ItemViewHolder(
                FilterEditorItemBinding.inflate(layoutInflater, parent, false)
            ) {
                getItem(it)?.run(onItemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FilterEditorUiModel.Header -> VIEW_TYPE_HEADER
            is FilterEditorUiModel.Item -> VIEW_TYPE_ITEM
        }
    }

    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        abstract fun bind(uiModel: FilterEditorUiModel)
    }

    private class HeaderViewHolder(
        private val binding: FilterEditorHeaderBinding,
        private val onHeaderClick: (Int) -> Unit
    ) : ViewHolder(binding.root) {

        init {
            binding.thumbnail.setOnDebounceClickListener {
                onHeaderClick(adapterPosition)
            }
        }

        override fun bind(uiModel: FilterEditorUiModel) {
            if (uiModel !is FilterEditorUiModel.Header) return
            binding.thumbnail.setImageURI(uiModel.imageUri)
        }
    }

    private class ItemViewHolder(
        private val binding: FilterEditorItemBinding,
        private val onItemClick: (Int) -> Unit
    ) : ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        override fun bind(uiModel: FilterEditorUiModel) {
            if (uiModel !is FilterEditorUiModel.Item) return
            binding.filterSelected.isVisible = uiModel.isSelected
            binding.thumbnail.setImageURI(uiModel.imageUri)
            binding.label.text = uiModel.filter.id
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_ITEM = 2
    }
}
