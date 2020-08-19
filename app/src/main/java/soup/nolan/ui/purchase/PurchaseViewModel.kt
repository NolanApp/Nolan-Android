package soup.nolan.ui.purchase

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData

class PurchaseViewModel @ViewModelInject constructor(
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
    }

    fun onNotPurchased(item: PurchaseItem) = when (item) {
        PurchaseItem.NoAds -> {
            appSettings.noAds = false
            _noAdsPurchased.value = false
        }
    }
}
