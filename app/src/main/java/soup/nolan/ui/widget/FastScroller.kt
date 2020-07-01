package soup.nolan.ui.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import soup.nolan.databinding.PhotoPickerScrollerBinding
import kotlin.math.roundToInt

class FastScroller @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = PhotoPickerScrollerBinding.inflate(LayoutInflater.from(context), this, true)

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            updateBubbleAndHandlePosition()
        }
    }

    var recyclerView: RecyclerView? = null
        set(value) {
            field = value
            field?.run {
                addOnScrollListener(scrollListener)
            }
        }

    private var viewHeight: Int = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        showScroller(event)
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> isTouchScroller(event)
            MotionEvent.ACTION_MOVE -> true
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> false
            else -> super.onTouchEvent(event)
        }
    }

    private fun isTouchScroller(event: MotionEvent): Boolean {
        val scrollerRect = Rect().apply {
            binding.viewScroller.getHitRect(this)
        }
        return scrollerRect.contains(event.x.toInt(), event.y.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
    }

    private fun showScroller(event: MotionEvent) {
        setScrollerPosition(event.y)
        setRecyclerViewPosition(event.y)
    }

    private fun setScrollerPosition(positionY: Float) {
        binding.viewScroller.y = getValueInRange(
            positionY - (binding.viewScroller.height / 2),
            viewHeight - binding.viewScroller.height
        )
    }

    private fun setRecyclerViewPosition(positionY: Float) {
        val recyclerView = recyclerView ?: return
        recyclerView.adapter?.run {
            val proportion: Float = when {
                binding.viewScroller.y == 0f -> 0f
                binding.viewScroller.y + binding.viewScroller.height >= viewHeight - SCROLLER_MAX_POSITION_GAP -> 1f
                else -> positionY / viewHeight
            }
            val targetPos: Float = getValueInRange(proportion * itemCount, itemCount - 1)
            (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(targetPos.roundToInt(), 0)
        }
    }

    private fun getValueInRange(value: Float, max: Int): Float {
        return value.coerceIn(0f, max.toFloat())
    }

    override fun onDetachedFromWindow() {
        recyclerView?.removeOnScrollListener(scrollListener)
        super.onDetachedFromWindow()
    }

    private fun updateBubbleAndHandlePosition() {
        recyclerView?.let {
            val scrollOffset = it.computeVerticalScrollOffset()
            val scrollRange = it.computeVerticalScrollRange()
            val proportion = scrollOffset.toFloat() / (scrollRange.toFloat() - viewHeight)
            setScrollerPosition(viewHeight * proportion)
        }
    }

    companion object {
        private const val SCROLLER_MAX_POSITION_GAP: Long = 5
    }
}
