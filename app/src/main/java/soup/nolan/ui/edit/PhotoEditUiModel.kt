package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.net.Uri

sealed class PhotoEditUiEvent {
    class Save(val bitmap: Bitmap) : PhotoEditUiEvent()
    class GoToCrop(val fileUri: Uri) : PhotoEditUiEvent()
    class GoToShare(val fileUri: Uri) : PhotoEditUiEvent()
    class ShowToast(val message: String) : PhotoEditUiEvent()
}
