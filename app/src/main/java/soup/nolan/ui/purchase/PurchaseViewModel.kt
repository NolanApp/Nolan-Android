package soup.nolan.ui.purchase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import javax.inject.Inject

class PurchaseViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _noAdsPurchased = MutableLiveData<Boolean>()
    val noAdsPurchased: LiveData<Boolean>
        get() = _noAdsPurchased

    private val _purchaseUpdateEvent = MutableEventLiveData<Unit>()
    val purchaseUpdateEvent: EventLiveData<Unit>
        get() = _purchaseUpdateEvent

    private val _purchaseItemEvent = MutableEventLiveData<PurchaseItem>()
    val purchaseItemEvent: EventLiveData<PurchaseItem>
        get() = _purchaseItemEvent

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _noAdsPurchased.postValue(appSettings.noAds)
        }
    }

    fun onPurchaseUpdate() {
        _purchaseUpdateEvent.event = Unit
    }

    fun purchase(item: PurchaseItem) {
        _purchaseItemEvent.event = item
    }

    fun onPurchased(item: PurchaseItem) = when (item) {
        PurchaseItem.NoAds -> {
            appSettings.noAds = true
            _noAdsPurchased.value = true
        }
        PurchaseItem.BuyCoffee01 -> appSettings.buyCoffee01 = true
        PurchaseItem.BuyCoffee02 -> appSettings.buyCoffee02 = true
        PurchaseItem.BuyCoffee03 -> appSettings.buyCoffee03 = true
        PurchaseItem.BuyCoffee04 -> appSettings.buyCoffee04 = true
        PurchaseItem.BuyCoffee05 -> appSettings.buyCoffee05 = true
    }

    fun onNotPurchased(item: PurchaseItem) = when (item) {
        PurchaseItem.NoAds -> {
            appSettings.noAds = false
            _noAdsPurchased.value = false
        }
        PurchaseItem.BuyCoffee01 -> appSettings.buyCoffee01 = false
        PurchaseItem.BuyCoffee02 -> appSettings.buyCoffee02 = false
        PurchaseItem.BuyCoffee03 -> appSettings.buyCoffee03 = false
        PurchaseItem.BuyCoffee04 -> appSettings.buyCoffee04 = false
        PurchaseItem.BuyCoffee05 -> appSettings.buyCoffee05 = false
    }
}
