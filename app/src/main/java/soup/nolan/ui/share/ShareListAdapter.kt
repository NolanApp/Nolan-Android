package soup.nolan.ui.share

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.R
import soup.nolan.databinding.ShareItemBinding
import soup.nolan.ui.utils.LauncherIcons
import soup.nolan.ui.utils.setOnDebounceClickListener
import timber.log.Timber

class ShareListAdapter(
    private val clickListener: (ShareItemUiModel) -> Unit
) : RecyclerView.Adapter<ShareListAdapter.ViewHolder>() {

    private val items = mutableListOf<ShareItemUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.share_item, parent, false)
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

    private fun getItem(position: Int): ShareItemUiModel? {
        return items.getOrNull(position)
    }

    fun submitList(list: List<ShareItemUiModel>) {
        Timber.d("submitList=$list")
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ShareItemBinding.bind(view)

        fun bind(item: ShareItemUiModel) {
            binding.logo.setAppIcon(item.packageName)
            binding.description.text = item.target.name
        }

        private fun ImageView.setAppIcon(packageName: String) {
            setImageDrawable(LauncherIcons.getAppIcon(context, packageName))
        }
    }
}
