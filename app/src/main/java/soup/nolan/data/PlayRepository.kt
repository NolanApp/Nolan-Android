package soup.nolan.data

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import soup.nolan.data.PlayRepository.Companion.UNKNOWN_VERSION_CODE

interface PlayRepository {
    suspend fun getAvailableVersionCode(): Int

    suspend fun requestReview(): ReviewInfo
    suspend fun launchReview(activity: Activity, reviewInfo: ReviewInfo)

    companion object {
        const val UNKNOWN_VERSION_CODE = 0
    }
}

class PlayRepositoryImpl(context: Context) : PlayRepository {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)

    private val reviewManager = ReviewManagerFactory.create(context)

    override suspend fun getAvailableVersionCode(): Int {
        return try {
            appUpdateManager.requestAppUpdateInfo().availableVersionCode()
        } catch (e: Exception) {
            UNKNOWN_VERSION_CODE
        }
    }

    override suspend fun requestReview(): ReviewInfo {
        return reviewManager.requestReview()
    }

    override suspend fun launchReview(activity: Activity, reviewInfo: ReviewInfo) {
        return reviewManager.launchReview(activity, reviewInfo)
    }
}
