package soup.nolan.firebase

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import soup.nolan.model.CameraFilter

class AppEvent(context: Context, private val screenName: String) {

    private val analytics = FirebaseAnalytics.getInstance(context.applicationContext)

    fun sendScreenEvent(fragment: Fragment) {
        analytics.setCurrentScreen(fragment.requireActivity(), screenName, null /* class override */)
    }

    fun sendButtonClick(name: String) {
        val bundle = bundleOf(
            "screen_name" to screenName,
            FirebaseAnalytics.Param.ITEM_ID to name,
            FirebaseAnalytics.Param.CONTENT_TYPE to "button"
        )
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun sendFilterSelect(filter: CameraFilter) {
        val bundle = bundleOf(
            "screen_name" to screenName,
            FirebaseAnalytics.Param.ITEM_ID to filter.id,
            FirebaseAnalytics.Param.CONTENT_TYPE to "filter"
        )
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }

    fun sendPromotionEvent(name: String) {
        val bundle = bundleOf(
            "screen_name" to screenName,
            FirebaseAnalytics.Param.ITEM_ID to name,
            FirebaseAnalytics.Param.CONTENT_TYPE to "ads"
        )
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_PROMOTION, bundle)
    }
}
