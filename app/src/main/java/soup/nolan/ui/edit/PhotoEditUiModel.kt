package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.annotation.StringRes

sealed class PhotoEditUiEvent {
    class Save(val bitmap: Bitmap) : PhotoEditUiEvent()
    class GoToCrop(val fileUri: Uri, val cropRect: Rect?) : PhotoEditUiEvent()
    class ShowShare(val bitmap: Bitmap) : PhotoEditUiEvent()
    class ShowToast(val message: String) : PhotoEditUiEvent()
    class ShowErrorToast(@StringRes val message: Int) : PhotoEditUiEvent()
}
