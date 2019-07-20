package soup.nolan.ui.utils

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

/** View */

@BindingAdapter("android:visibleIf")
fun setVisibleIf(view: View, predicate: Boolean) {
    view.isVisible = predicate
}

@BindingAdapter("android:invisibleIf")
fun setInvisibleIf(view: View, predicate: Boolean) {
    view.isInvisible = predicate
}

@BindingAdapter("android:goneIf")
fun setGoneIf(view: View, predicate: Boolean) {
    view.isGone = predicate
}
