package soup.nolan.ui.edit

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.MediaColumns
import android.text.format.DateUtils
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.contentValuesOf
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.NotificationChannels
import soup.nolan.R
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Gallery {

    private const val REQUEST_GET_SINGLE_FILE = 1

    fun takePicture(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        fragment.startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_GET_SINGLE_FILE
        )
    }

    fun onPictureTaken(requestCode: Int, resultCode: Int, data: Intent?, callback: (Uri) -> Unit) {
        if (requestCode == REQUEST_GET_SINGLE_FILE && resultCode == RESULT_OK) {
            data?.data?.run(callback)
        }
    }

    private const val DIR_NAME = "Nolan"
    private const val FILE_NAME_TEMPLATE = "Nolan_%s.png"

    suspend fun saveBitmap(context: Context, bitmap: Bitmap) {
        val imageUri = withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveBitmapQ(context, bitmap)
            } else {
                saveBitmapM(context, bitmap)
            }
        }
        if (imageUri == null) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            // Create the intent to show the stylized image in gallery
            val launchIntent = Intent(Intent.ACTION_VIEW)
            launchIntent.setDataAndType(imageUri, "image/png")
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

            val now = System.currentTimeMillis()

            val notificationBuilder = NotificationCompat
                .Builder(context, NotificationChannels.STYLIZES)
                .setContentTitle("Stylized image saved")
                .setContentText("Tap to view your stylized image")
                .setContentIntent(PendingIntent.getActivity(context, 0, launchIntent, 0))
                .setSmallIcon(R.drawable.ic_outline_image)
                .setWhen(now)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
                )
            val nm: NotificationManager? = context.getSystemService()
            nm?.notify(1, notificationBuilder.build())
        } catch (e: IOException) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun fileNameOf(imageTimeMs: Long): String {
        val imageDate = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date(imageTimeMs))
        return String.format(FILE_NAME_TEMPLATE, imageDate)
    }

    private fun saveBitmapM(context: Context, bitmap: Bitmap): Uri? {
        val imageTime = System.currentTimeMillis()
        val imageFileName = fileNameOf(imageTime)
        val directory = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), DIR_NAME)
        val imageFilePath = File(directory, imageFileName).absolutePath

        try {
            directory.mkdirs()

            val out = FileOutputStream(imageFilePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            val dateSeconds = imageTime / 1000

            val values = contentValuesOf(
                ImageColumns.DATA to imageFilePath,
                ImageColumns.TITLE to imageFileName,
                ImageColumns.DISPLAY_NAME to imageFileName,
                ImageColumns.DATE_ADDED to dateSeconds,
                ImageColumns.DATE_MODIFIED to dateSeconds,
                ImageColumns.MIME_TYPE to "image/png",
                ImageColumns.WIDTH to bitmap.width,
                ImageColumns.HEIGHT to bitmap.height,
                ImageColumns.SIZE to File(imageFilePath).length()
            )
            val resolver = context.contentResolver
            return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } catch (e: IOException) {
            Timber.w(e)
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun saveBitmapQ(context: Context, bitmap: Bitmap): Uri? {
        val imageTime = System.currentTimeMillis()
        val imageFileName = fileNameOf(imageTime)

        val dateSeconds = imageTime / 1000

        val values = contentValuesOf(
            MediaColumns.DISPLAY_NAME to imageFileName,
            MediaColumns.MIME_TYPE to "image/png",
            MediaColumns.DATE_ADDED to dateSeconds,
            MediaColumns.DATE_MODIFIED to dateSeconds,
            MediaColumns.IS_PENDING to 1,
            MediaColumns.DATE_EXPIRES to (imageTime + DateUtils.DAY_IN_MILLIS) / 1000,
            MediaColumns.RELATIVE_PATH to Environment.DIRECTORY_PICTURES
        )
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri == null) {
            Timber.w("saveBitmapQ: insert uri is null.")
            return null
        }
        try {
            val out = resolver.openOutputStream(uri)!!
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            val updateValues = contentValuesOf(
                MediaColumns.IS_PENDING to 0,
                MediaColumns.DATE_EXPIRES to null
            )
            resolver.update(uri, updateValues, null, null)
            return uri
        } catch (e: IOException) {
            resolver.delete(uri, null, null)
            Timber.w(e)
        }
        return null
    }

    fun createCacheFileUri(context: Context, filename: String): Uri {
        return File.createTempFile(filename, null, context.cacheDir).toUri()
    }
}
