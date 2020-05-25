package soup.nolan

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import soup.nolan.ui.utils.CrashlyticsTree
import timber.log.Timber

class NolanApplication : Application(), CameraXConfig.Provider {

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
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
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    companion object {

        internal lateinit var dependency: Dependency
    }
}
