package soup.nolan.ui.camera.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.R
import soup.nolan.databinding.CameraFilterItemBinding
import soup.nolan.ui.utils.IdBasedDiffCallback
import soup.nolan.ui.utils.setOnDebounceClickListener

class CameraFilterListAdapter(
    private val onItemSelect: (CameraFilterItemUiModel) -> Unit
) : ListAdapter<CameraFilterItemUiModel, CameraFilterListAdapter.ViewHolder>(IdBasedDiffCallback { it.id }) {

    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.camera_filter_item, parent, false)
        ).apply {
            itemView.setOnDebounceClickListener {
                getItem(adapterPosition)?.let {
                    setSelectedPosition(adapterPosition)
                    onItemSelect(it)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isSelected = position == selectedPosition)
    }

    fun setSelectedPosition(position: Int) {
        val lastPosition = selectedPosition
        if (lastPosition != position) {
            selectedPosition = position

            // update
            notifyItemChanged(lastPosition)
            notifyItemChanged(position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = CameraFilterItemBinding.bind(view)

        fun bind(item: CameraFilterItemUiModel, isSelected: Boolean) {
            itemView.isEnabled = isSelected.not()
            binding.filterThumbnail.setImageResource(item.thumbnailResId)
            binding.filterPick.isVisible = isSelected
            binding.filterLabel.text = item.id
        }
    }
}
