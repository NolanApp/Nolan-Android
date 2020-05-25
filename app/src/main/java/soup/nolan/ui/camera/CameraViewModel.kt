package soup.nolan.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import soup.nolan.ads.AdManager
import soup.nolan.settings.AppSettings
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData
import soup.nolan.ui.base.BaseViewModel
import javax.inject.Inject

class CameraViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val adManager: AdManager
) : BaseViewModel() {

    private val _lensFacingFront = MutableLiveData(appSettings.lensFacingFront)
    val lensFacingFront: LiveData<Boolean>
        get() = _lensFacingFront

    private val _gallerySelectableCount = MutableLiveData<Int>()
    val gallerySelectableCount: LiveData<Int>
        get() = _gallerySelectableCount

    private val _uiEvent = MutableEventLiveData<CameraUiEvent>()
    val uiEvent: EventLiveData<CameraUiEvent>
        get() = _uiEvent

    init {
        refreshRewardedAdIfNeeded()
        viewModelScope.launch(Dispatchers.IO) {
            delay(300)
            _gallerySelectableCount.postValue(appSettings.gallerySelectableCount)
        }
    }

    fun refresh() {
        refreshRewardedAdIfNeeded()
    }

    fun onLensFacingClick(lensFacingFront: Boolean) {
        appSettings.lensFacingFront = lensFacingFront.not()
        _lensFacingFront.value = lensFacingFront.not()
    }

    fun onGalleryButtonClick() {
        val currentCount = appSettings.gallerySelectableCount
        if (currentCount < 5) {
            viewModelScope.launch {
                adManager.loadNextRewardedAd()
            }
        }
        if (currentCount > 0) {
            appSettings.gallerySelectableCount = currentCount - 1
            _gallerySelectableCount.value = currentCount - 1
        }
        viewModelScope.launch(Dispatchers.IO) {
            delay(200)
            _uiEvent.postEvent(
                if (currentCount <= 0) {
                    CameraUiEvent.ShowAdDialog
                } else {
                    CameraUiEvent.GoToGallery
                }
            )
        }
    }

    fun onShowAdClick() {
        val rewardedAd = adManager.getLoadedRewardedAd()
        _uiEvent.event = if (rewardedAd != null) {
            CameraUiEvent.ShowAd(rewardedAd)
        } else {
            CameraUiEvent.ShowErrorToast
        }
    }

    fun onUserEarnedReward(rewardAmount: Int) {
        appSettings.gallerySelectableCount = rewardAmount
        _gallerySelectableCount.value = rewardAmount
        adManager.onRewardedAdConsumed()
    }

    fun onRewardedAdFailedToShow() {
        adManager.onRewardedAdConsumed()
    }

    fun onRewardedAdClosed() {
        val currentCount = appSettings.gallerySelectableCount
        if (currentCount > 0) {
            appSettings.gallerySelectableCount = currentCount - 1
            _gallerySelectableCount.value = currentCount - 1
            _uiEvent.event = CameraUiEvent.GoToGallery
        }
    }

    private fun refreshRewardedAdIfNeeded() {
        if (appSettings.gallerySelectableCount == 0) {
            viewModelScope.launch {
                adManager.loadNextRewardedAd()
            }
        }
    }
}
