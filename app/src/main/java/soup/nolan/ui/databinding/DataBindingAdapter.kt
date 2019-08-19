package soup.nolan.ui.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class DataBindingAdapter<T> : RecyclerView.Adapter<DataBindingViewHolder<T>>() {

    private var items = mutableListOf<T>()

    @LayoutRes
    protected abstract fun getLayoutResId(viewType: Int): Int

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, getLayoutResId(viewType), parent, false)
        return createViewHolder(binding)
    }

    protected open fun createViewHolder(binding: ViewDataBinding): DataBindingViewHolder<T> {
        return DataBindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) {
        holder.bind(getItem(position))
    }

    protected fun getItem(position: Int): T? {
        return items.getOrNull(position)
    }

    final override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(list: List<T>) {
        this.items = list.toMutableList()
        notifyDataSetChanged()
    }
}
