package soup.nolan.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageFactory(private val context: Context) {

    suspend fun getBitmap(fileUri: Uri): Bitmap {
        return withContext(Dispatchers.IO) {
            FirebaseVisionImage.fromFilePath(context, fileUri).bitmap
        }
    }
}
