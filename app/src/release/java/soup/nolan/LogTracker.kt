package soup.nolan

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import timber.log.Timber

object LogTracker {

    fun install(context: Context) {
        initCrashlytics(context.applicationContext)
        Timber.plant(CrashlyticsTree())
    }

    private fun initCrashlytics(context: Context) {
        val core = CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()
        Fabric.with(context, Crashlytics.Builder().core(core).build())
    }

    private class CrashlyticsTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return
            }
            when {
                t != null -> Crashlytics.logException(t)
                tag != null -> Crashlytics.log("$tag: $message")
                else -> Crashlytics.log(message)
            }
        }
    }
}
