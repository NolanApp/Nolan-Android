package soup.nolan

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import soup.nolan.di.DaggerApplicationComponent

class NolanApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        LogTracker.install(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }
}
