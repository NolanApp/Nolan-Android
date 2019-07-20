package soup.nolan.ui.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.EventObserver

inline fun <T> LiveData<T>.observeState(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    observe(owner, Observer { observer(it) })
}

inline fun <T> EventLiveData<T>.observeEvent(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    observe(owner, EventObserver { observer(it) })
}
