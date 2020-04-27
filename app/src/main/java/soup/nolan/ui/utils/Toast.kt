@file:Suppress("NOTHING_TO_INLINE")

package soup.nolan.ui.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

inline fun Fragment.toast(msg: CharSequence) {
    context?.toast(msg)
}

inline fun Fragment.toast(@StringRes msg: Int) {
    context?.toast(msg)
}

inline fun Context.toast(msg: CharSequence) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

inline fun Context.toast(@StringRes msg: Int) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
