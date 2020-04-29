package soup.nolan

import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import soup.nolan.di.DaggerApplicationComponent

class NolanApplication : DaggerApplication(), CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()
        LogTracker.install(this)
        NotificationChannels.createAll(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}
