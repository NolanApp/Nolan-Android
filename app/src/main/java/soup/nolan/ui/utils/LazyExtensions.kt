package soup.nolan.ui.utils

inline fun <T> lazyFast(crossinline initializer: () -> T): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        initializer()
    }
}
