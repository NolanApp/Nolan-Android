package soup.nolan.ui.share

import androidx.lifecycle.liveData
import soup.nolan.ui.base.BaseViewModel
import javax.inject.Inject

class ShareViewModel @Inject constructor() : BaseViewModel() {

    val shareList = liveData {
        emit(ThirdPartyApp.list().map { it.toUiModel() })
    }

    private fun ThirdPartyApp.toUiModel(): ShareItemUiModel {
        return ShareItemUiModel(ShareTarget.INSTAGRAM, packageNames.firstOrNull().orEmpty())
    }

    fun onShareClick(item: ShareItemUiModel) {
        //TODO:
    }
}
