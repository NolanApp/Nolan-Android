package soup.nolan.ui.settings

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.Dependency
import soup.nolan.data.PlayRepository
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.purchase.PurchaseItem

class SettingsViewModel(
    private val repository: PlayRepository = Dependency.playRepository,
    private val appSettings: AppSettings = Dependency.appSettings
) : ViewModel() {

    private val _showWatermark = MutableLiveData<Boolean>()
    val showWatermark: LiveData<Boolean>
        get() = _showWatermark

    private val _removeAdsUiModel = MutableLiveData<RemoveAdsUiModel>()
    val removeAdsUiModel: LiveData<RemoveAdsUiModel>
        get() = _removeAdsUiModel

    private val _toastEvent = MutableEventLiveData<ToastEvent>()
    val toastEvent: EventLiveData<ToastEvent>
        get() = _toastEvent

    private val _purchaseItemEvent = MutableEventLiveData<PurchaseItem>()
    val purchaseItemEvent: EventLiveData<PurchaseItem>
        get() = _purchaseItemEvent

    val latestVersionCode: LiveData<Int> = liveData(Dispatchers.IO) {
        emit(repository.getAvailableVersionCode())
    }

    init {
        updateDataForPurchase()
        viewModelScope.launch(Dispatchers.IO) {
            _showWatermark.postValue(appSettings.showWatermark)
        }
    }

    fun onPurchaseUpdated() {
        updateDataForPurchase()
    }

    private fun updateDataForPurchase() {
        _removeAdsUiModel.value = RemoveAdsUiModel(
            isPurchased = appSettings.noAds
        )
    }

    fun onRemoveAdsClick() {
        if (appSettings.noAds) {
            _toastEvent.event = ToastEvent.AlreadyNoAdsPurchased
        } else {
            _purchaseItemEvent.event = PurchaseItem.NoAds
        }
    }

    fun onShowWatermarkChecked(checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettings.showWatermark = checked
            _showWatermark.postValue(checked)
        }
    }
}
