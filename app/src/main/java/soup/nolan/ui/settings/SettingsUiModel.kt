package soup.nolan.ui.settings

data class RemoveAdsUiModel(
    val isPurchased: Boolean
)

data class BuyCoffeeUiModel(
    val isVisible: Boolean,
    val isPurchased01: Boolean,
    val isPurchased02: Boolean,
    val isPurchased03: Boolean,
    val isPurchased04: Boolean,
    val isPurchased05: Boolean
)

sealed class ToastUiModel {
    object AlreadyNoAdsPurchased : ToastUiModel()
    object AlreadyBuyAllCoffees : ToastUiModel()
}
