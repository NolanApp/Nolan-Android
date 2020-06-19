package soup.nolan.ui.settings

import androidx.annotation.StringRes
import soup.nolan.R

data class RemoveAdsUiModel(
    val isPurchased: Boolean
)

sealed class ToastEvent(@StringRes val msg: Int) {
    object AlreadyNoAdsPurchased : ToastEvent(R.string.toast_already_no_ads_purchased)
}
