package soup.nolan.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import soup.nolan.data.PlayRepository
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.base.BaseViewModel
import soup.nolan.ui.purchase.PurchaseItem
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val repository: PlayRepository,
    private val appSettings: AppSettings
) : BaseViewModel() {

    val latestVersionCode: LiveData<Int> = liveData {
        emit(repository.getAvailableVersionCode())
    }

    private val _removeAdsUiModel = MutableLiveData<RemoveAdsUiModel>()
    val removeAdsUiModel: LiveData<RemoveAdsUiModel>
        get() = _removeAdsUiModel

    private val _buyCoffeeUiModel = MutableLiveData<BuyCoffeeUiModel>()
    val buyCoffeeUiModel: LiveData<BuyCoffeeUiModel>
        get() = _buyCoffeeUiModel

    private val _toastEvent = MutableEventLiveData<ToastUiModel>()
    val toastEvent: EventLiveData<ToastUiModel>
        get() = _toastEvent

    private val _purchaseItemEvent = MutableEventLiveData<PurchaseItem>()
    val purchaseItemEvent: EventLiveData<PurchaseItem>
        get() = _purchaseItemEvent

    init {
        updateListForPurchase()
    }

    fun onPurchaseUpdated() {
        updateListForPurchase()
    }

    private fun updateListForPurchase() {
        _removeAdsUiModel.value = RemoveAdsUiModel(
            isPurchased = appSettings.noAds
        )
        _buyCoffeeUiModel.value = BuyCoffeeUiModel(
            isVisible = appSettings.noAds,
            isPurchased01 = appSettings.buyCoffee01,
            isPurchased02 = appSettings.buyCoffee02,
            isPurchased03 = appSettings.buyCoffee03,
            isPurchased04 = appSettings.buyCoffee04,
            isPurchased05 = appSettings.buyCoffee05
        )
    }

    fun onRemoveAdsClick() {
        if (appSettings.noAds) {
            _toastEvent.event = ToastUiModel.AlreadyNoAdsPurchased
        } else {
            _purchaseItemEvent.event = PurchaseItem.NoAds
        }
    }

    fun onBuyCoffeeClick() {
        when {
            appSettings.buyCoffee01.not() ->
                _purchaseItemEvent.event = PurchaseItem.BuyCoffee01
            appSettings.buyCoffee02.not() ->
                _purchaseItemEvent.event = PurchaseItem.BuyCoffee02
            appSettings.buyCoffee03.not() ->
                _purchaseItemEvent.event = PurchaseItem.BuyCoffee03
            appSettings.buyCoffee04.not() ->
                _purchaseItemEvent.event = PurchaseItem.BuyCoffee04
            appSettings.buyCoffee05.not() ->
                _purchaseItemEvent.event = PurchaseItem.BuyCoffee05
            else -> {
                _toastEvent.event = ToastUiModel.AlreadyBuyAllCoffees
            }
        }
    }
}
