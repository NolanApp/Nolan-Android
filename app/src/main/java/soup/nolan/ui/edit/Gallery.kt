package soup.nolan.ui.edit

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import soup.nolan.NotificationChannels
import soup.nolan.R
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

    private const val DIR_NAME = "Stylizes"
    private const val FILE_NAME_TEMPLATE = "Stylize_%s.png"

    fun saveBitmap(context: Context, bitmap: Bitmap) {
        val imageTime = System.currentTimeMillis()
        val imageDate = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date(imageTime))
        val imageFileName = String.format(FILE_NAME_TEMPLATE, imageDate)

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

            // Save the stylized image to the MediaStore
            val values = ContentValues()
            val resolver = context.contentResolver
            values.put(MediaStore.Images.ImageColumns.DATA, imageFilePath)
            values.put(MediaStore.Images.ImageColumns.TITLE, imageFileName)
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, imageFileName)
            values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, imageTime)
            values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds)
            values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds)
            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png")
            values.put(MediaStore.Images.ImageColumns.WIDTH, bitmap.width)
            values.put(MediaStore.Images.ImageColumns.HEIGHT, bitmap.height)
            values.put(MediaStore.Images.ImageColumns.SIZE, File(imageFilePath).length())
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

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
}
