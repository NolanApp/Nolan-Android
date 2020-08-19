package soup.nolan.ui.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.databinding.PhotoPickerItemBinding
import soup.nolan.ui.utils.GlideApp
import soup.nolan.ui.utils.IdBasedDiffCallback
import soup.nolan.ui.utils.setOnDebounceClickListener

class PhotoPickerListAdapter(
    private val onItemClick: (PhotoPickerItemUiModel) -> Unit
) : ListAdapter<PhotoPickerItemUiModel, PhotoViewHolder>(IdBasedDiffCallback { it.uri.toString() }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhotoPickerItemBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding).apply {
            itemView.setOnDebounceClickListener {
                onItemClick(getItem(bindingAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: PhotoViewHolder) {
        holder.recycled()
        super.onViewRecycled(holder)
    }
}

class PhotoViewHolder(
    private val binding: PhotoPickerItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(uiModel: PhotoPickerItemUiModel) {
        GlideApp.with(itemView.context)
            .load(uiModel.uri)
            .thumbnail(0.1f)
            .into(binding.photoThumbnail)
    }

    fun recycled() {
        GlideApp.with(itemView).clear(binding.photoThumbnail)
    }
}

