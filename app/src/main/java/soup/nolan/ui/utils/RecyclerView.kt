package soup.nolan.ui.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class IdBasedDiffCallback<T : Any>(
    private val id: (T) -> String
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return id(oldItem) == id(newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return java.util.Objects.equals(oldItem, newItem)
    }
}

fun RecyclerView.scrollToPositionInCenter(position: Int) {
    val layoutManager = layoutManager as? LinearLayoutManager
    if (layoutManager == null) {
        Timber.w("scrollToPositionInCenter: layoutManager is not LinearLayoutManager!")
        return
    }
    val child = if (layoutManager.childCount > 0) {
        layoutManager.getChildAt(0)
    } else {
        null
    }
    when (layoutManager.orientation) {
        LinearLayoutManager.HORIZONTAL -> {
            val centerOfScreen: Int = (width - (child?.width ?: 0)) / 2
            layoutManager.scrollToPositionWithOffset(position, centerOfScreen)
        }
        LinearLayoutManager.VERTICAL -> {
            val centerOfScreen: Int = (height - (child?.height ?: 0)) / 2
            layoutManager.scrollToPositionWithOffset(position, centerOfScreen)
        }
    }
}
