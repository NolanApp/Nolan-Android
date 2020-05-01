package soup.nolan.ui.edit.crop

import android.graphics.Rect
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CroppedPhoto(
    val fileUri: Uri,
    val cropRect: Rect
) : Parcelable
