package soup.nolan.ui.camera

import com.google.android.gms.ads.rewarded.RewardedAd

sealed class CameraUiEvent {

    object ShowAdDialog : CameraUiEvent()

    class ShowAd(val rewardedAd: RewardedAd) : CameraUiEvent()

    object ShowErrorToast : CameraUiEvent()

    object GoToGallery : CameraUiEvent()
}
