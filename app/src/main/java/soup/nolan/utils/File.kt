package soup.nolan.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import soup.nolan.BuildConfig
import java.io.File
import java.io.FileOutputStream

fun File.write(
    bitmap: Bitmap,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 100
): File {
    bitmap.compress(format, quality, FileOutputStream(this))
    return this
}

fun File.toContentUri(context: Context): Uri {
    return FileProvider.getUriForFile(context, BuildConfig.FILES_AUTHORITY, this)
}
