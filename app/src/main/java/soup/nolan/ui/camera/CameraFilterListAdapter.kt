package soup.nolan.ui.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.R
import soup.nolan.databinding.CameraItemFilterBinding
import soup.nolan.ui.utils.setOnDebounceClickListener

class CameraFilterListAdapter(
    private val clickListener: (CameraFilterUiModel) -> Unit
) : RecyclerView.Adapter<CameraFilterListAdapter.ViewHolder>() {

    private val items = mutableListOf<CameraFilterUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.camera_item_filter, parent, false)
        ).apply {
            itemView.setOnDebounceClickListener {
                getItem(adapterPosition)?.run(clickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): CameraFilterUiModel? {
        return items.getOrNull(position)
    }

    fun submitList(list: List<CameraFilterUiModel>) {
        this.items.clear()
        this.items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = CameraItemFilterBinding.bind(view)

        fun bind(item: CameraFilterUiModel) {
            binding.thumbnailView.setImageResource(item.thumbnailResId)
        }
    }
}
