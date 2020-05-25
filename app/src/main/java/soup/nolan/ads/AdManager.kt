package soup.nolan.ads

import com.google.android.gms.ads.rewarded.RewardedAd

interface AdManager {

    fun getLoadedRewardedAd(): RewardedAd?

    suspend fun loadNextRewardedAd()

    fun onRewardedAdConsumed()
}
