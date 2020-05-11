package soup.nolan.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import soup.nolan.R
import timber.log.Timber

class AdManagerImpl(private val context: Context) : AdManager {

    private val adUnitId: String = context.getString(R.string.admob_ad_unit_reward)

    enum class State {
        LOADED, CONSUMED
    }
    private var state: State = State.CONSUMED

    private var lastRewardedAd: RewardedAd? = null

    init {
        MobileAds.initialize(context)
    }

    override fun getLoadedRewardedAd(): RewardedAd? {
        Timber.d("getLoadedRewardedAd: isLoaded=${lastRewardedAd?.isLoaded}, state=$state")
        return lastRewardedAd?.takeIf { it.isLoaded }
    }

    override fun loadNextRewardedAd() {
        if (lastRewardedAd?.isLoaded == true && state == State.LOADED) {
            return
        }
        val rewardedAd = RewardedAd(context, adUnitId)
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                lastRewardedAd = rewardedAd
                state = State.LOADED
                Timber.d("onRewardedAdLoaded: State.LOADED")
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Timber.w("onAdFailedToLoad: errorCode=$errorCode")
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    override fun onRewardedAdConsumed() {
        Timber.d("onRewardedAdConsumed: State.CONSUMED")
        state = State.CONSUMED
    }
}
