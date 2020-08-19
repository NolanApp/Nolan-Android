package soup.nolan.ui.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class AlwaysDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = false
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = false
}

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

class GridSpaceItemDecoration(
    private val spanCount: Int,
    private val space: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        if (position >= 0) {
            val column = position % spanCount // item column
            outRect.left = column * space / spanCount
            outRect.right = space - (column + 1) * space / spanCount
            if (position >= spanCount) {
                outRect.top = space // item top
            }
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }
    }
}
