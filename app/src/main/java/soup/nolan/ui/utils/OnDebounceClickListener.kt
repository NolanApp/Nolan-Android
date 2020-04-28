package soup.nolan.ui.utils

import android.os.SystemClock
import android.view.View

private typealias OnClickListener = (View) -> Unit

fun View.setOnDebounceClickListener(listener: OnClickListener?) {
    if (listener == null) {
        setOnClickListener(null)
    } else {
        setOnClickListener(OnDebounceClickListener(listener))
    }
}

class OnDebounceClickListener(private val listener: OnClickListener) : View.OnClickListener {

    private var lastTime: Long = 0

    override fun onClick(v: View?) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastTime < INTERVAL) return
        lastTime = now
        if (v != null) {
            listener(v)
        }
    }

    companion object {

        private const val INTERVAL: Long = 300
    }
}
