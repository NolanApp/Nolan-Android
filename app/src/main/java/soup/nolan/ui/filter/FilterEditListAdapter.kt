package soup.nolan.ui.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.R
import soup.nolan.databinding.FilterEditHeaderBinding
import soup.nolan.databinding.FilterEditItemBinding
import soup.nolan.model.thumbnailResId
import soup.nolan.ui.utils.IdBasedDiffCallback
import soup.nolan.ui.utils.setOnDebounceClickListener

class FilterEditListAdapter(
    private val onItemClick: (FilterEditUiModel) -> Unit
) : ListAdapter<FilterEditUiModel, FilterEditListAdapter.ViewHolder>(IdBasedDiffCallback { it.key }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(
                FilterEditHeaderBinding
                    .inflate(layoutInflater, parent, false)
            ) {
                getItem(it)?.run(onItemClick)
            }
        } else {
            ItemViewHolder(
                FilterEditItemBinding
                    .inflate(layoutInflater, parent, false)
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
            is FilterEditUiModel.Header -> VIEW_TYPE_HEADER
            is FilterEditUiModel.Item -> VIEW_TYPE_ITEM
        }
    }

    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        abstract fun bind(uiModel: FilterEditUiModel)
    }

    private class HeaderViewHolder(
        private val binding: FilterEditHeaderBinding,
        private val onHeaderClick: (Int) -> Unit
    ) : ViewHolder(binding.root) {

        init {
            binding.thumbnail.setOnDebounceClickListener {
                onHeaderClick(adapterPosition)
            }
        }

        override fun bind(uiModel: FilterEditUiModel) {
            if (uiModel !is FilterEditUiModel.Header) return
            binding.thumbnail.setImageResource(R.drawable.default_filter_origin)
        }
    }

    private class ItemViewHolder(
        private val binding: FilterEditItemBinding,
        private val onItemClick: (Int) -> Unit
    ) : ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        override fun bind(uiModel: FilterEditUiModel) {
            if (uiModel !is FilterEditUiModel.Item) return
            binding.filterSelected.isVisible = uiModel.isSelected
            binding.thumbnail.setImageResource(uiModel.filter.thumbnailResId)
            binding.label.text = uiModel.id
        }
    }


    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_ITEM = 2
    }
}
