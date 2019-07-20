package soup.nolan

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import soup.nolan.di.DaggerApplicationComponent
import timber.log.Timber

class NolanApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        //TODO: Improve this before release
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }
}
