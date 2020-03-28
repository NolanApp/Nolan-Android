package soup.nolan.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import soup.nolan.R
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdManagerImpl(private val context: Context) : AdManager {

    private val adUnitId: String = context.getString(R.string.admob_ad_unit_reward)

    init {
        MobileAds.initialize(context)
    }

    override suspend fun loadRewardedAd(): RewardedAd? {
        return suspendCoroutine { continuation ->
            val rewardedAd = RewardedAd(context, adUnitId)
            val adLoadCallback = object: RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    continuation.resume(rewardedAd)
                }
                override fun onRewardedAdFailedToLoad(errorCode: Int) {
                    Timber.w("onAdFailedToLoad: errorCode=$errorCode")
                    continuation.resume(null)
                }
            }
            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        }
    }
}
