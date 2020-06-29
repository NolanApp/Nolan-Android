package soup.nolan.ui.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import soup.nolan.Dependency
import soup.nolan.data.ShareRepository
import soup.nolan.model.ThirdPartyApp

class ShareViewModel(
    private val repository: ShareRepository = Dependency.shareRepository
) : ViewModel() {

    val shareList = liveData {
        val appList = repository.getInstalledAppSet()
        val shareList = mutableListOf<ShareItemUiModel>()
        if (ThirdPartyApp.KakaoTalk in appList) {
            shareList.add(ShareItemUiModel.KakaoTalk)
        }
        if (ThirdPartyApp.Instagram in appList) {
            shareList.add(ShareItemUiModel.Instagram)
        }
        if (ThirdPartyApp.Facebook in appList) {
            shareList.add(ShareItemUiModel.Facebook)
        }
        if (ThirdPartyApp.Line in appList) {
            shareList.add(ShareItemUiModel.Line)
        }
        if (ThirdPartyApp.WhatsApp in appList) {
            shareList.add(ShareItemUiModel.WhatsApp)
        }
        if (ThirdPartyApp.Twitter in appList) {
            shareList.add(ShareItemUiModel.Twitter)
        }
        shareList.add(ShareItemUiModel.More)
        emit(shareList)
    }
}
