package soup.nolan.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        Timber.d("onNewToken: $newToken")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("onMessageReceived: from=${remoteMessage.from}, data=${remoteMessage.data}")
    }
}
