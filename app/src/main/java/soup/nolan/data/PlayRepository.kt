package soup.nolan.data

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.requestAppUpdateInfo
import soup.nolan.data.PlayRepository.Companion.UNKNOWN_VERSION_CODE

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
}
