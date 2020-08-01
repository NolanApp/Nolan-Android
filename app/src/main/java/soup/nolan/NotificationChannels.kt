package soup.nolan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {

    const val NOTICE = "NOTICE"
    const val SAVE = "STYLIZES"

    fun createAll(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notice = NotificationChannel(
                NOTICE,
                context.getString(R.string.notification_channel_notice),
                NotificationManager.IMPORTANCE_HIGH
            )
            val stylizes = NotificationChannel(
                SAVE,
                context.getString(R.string.notification_channel_save),
                NotificationManager.IMPORTANCE_HIGH
            )
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannels(listOf(notice, stylizes))
        }
    }
}
