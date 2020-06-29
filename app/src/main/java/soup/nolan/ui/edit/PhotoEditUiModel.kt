package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.annotation.StringRes
import soup.nolan.ui.share.ShareItemUiModel

sealed class PhotoEditUiEvent {
    class Save(val bitmap: Bitmap) : PhotoEditUiEvent()
    class Share(val uiModel: ShareItemUiModel, val shareImageUri: Uri) : PhotoEditUiEvent()
    class GoToCrop(val fileUri: Uri, val cropRect: Rect?) : PhotoEditUiEvent()
    object ShowShare : PhotoEditUiEvent()
    class ShowToast(val message: String) : PhotoEditUiEvent()
    class ShowErrorToast(@StringRes val message: Int) : PhotoEditUiEvent()
}
