package soup.nolan

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import soup.nolan.model.Appearance
import soup.nolan.ui.utils.CrashlyticsTree
import timber.log.Timber

class NolanApplication : Application(), CameraXConfig.Provider {

    override fun onCreate() {
        if (BuildConfig.USE_STRICT_MODE) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
        }
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
        NotificationChannels.createAll(this)

        dependency = Dependency(this)

        GlobalScope.launch(Dispatchers.IO) {
            val nightMode = Appearance.of(Dependency.appSettings.currentAppearance).nightMode
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    companion object {

        internal lateinit var dependency: Dependency
    }
}
