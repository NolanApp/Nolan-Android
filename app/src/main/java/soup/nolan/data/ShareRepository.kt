package soup.nolan.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import soup.nolan.model.ThirdPartyApp

interface ShareRepository {
    suspend fun getInstalledAppSet(): Set<ThirdPartyApp>
}

class ShareRepositoryImpl(private val context: Context) : ShareRepository {

    override suspend fun getInstalledAppSet(): Set<ThirdPartyApp> {
        return withContext(Dispatchers.Default) {
            ThirdPartyApp.list().filter { it.isInstalled(context) }.toSet()
        }
    }
}
