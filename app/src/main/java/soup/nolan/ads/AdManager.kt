package soup.nolan.ads

import com.google.android.gms.ads.rewarded.RewardedAd

interface AdManager {

    suspend fun loadRewardedAd(): RewardedAd?
}
