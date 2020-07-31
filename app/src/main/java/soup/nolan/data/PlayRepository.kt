package soup.nolan.data

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import soup.nolan.data.PlayRepository.Companion.UNKNOWN_VERSION_CODE
import kotlin.coroutines.suspendCoroutine

interface PlayRepository {
    suspend fun getAvailableVersionCode(): Int

    companion object {
        const val UNKNOWN_VERSION_CODE = 0
    }
}

class PlayRepositoryImpl(context: Context) : PlayRepository {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)

    override suspend fun getAvailableVersionCode(): Int {
        return try {
            appUpdateManager.requestAppUpdateInfo()
                .availableVersionCode()
        } catch (e: Exception) {
            UNKNOWN_VERSION_CODE
        }
    }

    private suspend fun AppUpdateManager.requestAppUpdateInfo(): AppUpdateInfo {
        return suspendCoroutine { continuation ->
            appUpdateInfo
                .addOnSuccessListener {
                    continuation.resumeWith(Result.success(it))
                }
                .addOnFailureListener {
                    continuation.resumeWith(Result.failure(it))
                }
        }
    }
}
