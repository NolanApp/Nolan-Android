package soup.nolan.ui.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.databinding.ShareItemBinding
import soup.nolan.ui.utils.clipToOval
import soup.nolan.ui.utils.setOnDebounceClickListener

class ShareListAdapter(
    private val clickListener: (ShareItemUiModel) -> Unit
) : RecyclerView.Adapter<ShareListAdapter.ViewHolder>() {

    private val items = mutableListOf<ShareItemUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ShareItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ).apply {
            itemView.setOnDebounceClickListener {
                getItem(bindingAdapterPosition)?.run(clickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): ShareItemUiModel? {
        return items.getOrNull(position)
    }

    fun submitList(list: List<ShareItemUiModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ShareItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.logo.clipToOval(true)
        }

        fun bind(item: ShareItemUiModel) {
            binding.logo.setImageDrawable(item.getIcon(itemView.context))
            binding.description.text = item.getLabel(itemView.context)
        }
    }
}
