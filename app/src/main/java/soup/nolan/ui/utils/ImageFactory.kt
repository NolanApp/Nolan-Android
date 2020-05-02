package soup.nolan.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class ImageFactory(private val context: Context) {

    fun getBitmap(fileUri: Uri): Bitmap {
        return FirebaseVisionImage.fromFilePath(context, fileUri).bitmap
    }
}
