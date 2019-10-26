package soup.nolan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {

    const val STYLIZES = "STYLIZES"

    fun createAll(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val stylizes = NotificationChannel(
                STYLIZES,
                "Stylizes",
                NotificationManager.IMPORTANCE_HIGH
            )
            val nm = context.getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannels(listOf(stylizes))
        }
    }
}
