package soup.nolan.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import soup.nolan.R

class CheckedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), Checkable {

    interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a checked imageview has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        fun onCheckedChanged(buttonView: CheckedImageView, isChecked: Boolean)
    }

    private var checked: Boolean = false
    private var broadcasting: Boolean = false

    private var listener: OnCheckedChangeListener? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CheckedImageView, defStyleAttr, 0)
        try {
            isChecked = a.getBoolean(R.styleable.CheckedImageView_checked, false)
        } finally {
            a.recycle()
        }
    }

    override fun toggle() {
        isChecked = !checked
    }

    override fun isChecked(): Boolean {
        return checked
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            this.checked = checked
            refreshDrawableState()

            // Avoid infinite recursions if setChecked() is called from a listener
            if (broadcasting) {
                return
            }

            broadcasting = true
            listener?.onCheckedChanged(this, this.checked)

            broadcasting = false
        }
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        this.listener = listener
    }

    fun setOnCheckedChangeListener(listener: (CheckedImageView, Boolean) -> Unit) {
        this.listener = object : OnCheckedChangeListener {

            override fun onCheckedChanged(buttonView: CheckedImageView, isChecked: Boolean) =
                listener(buttonView, isChecked)
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(
                drawableState,
                CHECKED_STATE_SET
            )
        }
        return drawableState
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = CheckedImageView::class.java.name
        event.isChecked = checked
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = CheckedImageView::class.java.name
        info.isCheckable = true
        info.isChecked = checked
    }

    companion object {

        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}
