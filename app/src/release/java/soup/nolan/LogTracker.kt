package soup.nolan

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

object LogTracker {

    private const val E_TAG = "E/TAG:"

    private val crashlytics = FirebaseCrashlytics.getInstance()

    fun install(context: Context) {
        Timber.plant(CrashlyticsTree())
    }

    private class CrashlyticsTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) return
            when (priority) {
                Log.WARN -> when {
                    t != null -> crashlytics.recordException(t)
                    tag != null -> crashlytics.log("$tag: $message")
                    else -> crashlytics.log(message)
                }
                Log.ERROR, Log.ASSERT -> when {
                    t != null -> crashlytics.recordException(t)
                    tag != null -> crashlytics.log("$E_TAG $tag: $message")
                    else -> crashlytics.log("$E_TAG $message")
                }
            }
        }
    }
}
