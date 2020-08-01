package soup.nolan.ui.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
    if (this.value != newValue) value = newValue
}

fun <T> MutableLiveData<T>.postValueIfNew(newValue: T) {
    if (this.value != newValue) postValue(newValue)
}

class NonNullObserver<T>(
    private val onNonNullChanged: (T) -> Unit
) : Observer<T?> {

    override fun onChanged(t: T?) {
        t?.run(onNonNullChanged)
    }
}
