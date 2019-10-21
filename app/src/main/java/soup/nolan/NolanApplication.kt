package soup.nolan

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.alterac.blurkit.BlurKit
import soup.nolan.di.DaggerApplicationComponent
import soup.nolan.stylize.experimental.StyleTransfer

class NolanApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        LogTracker.install(this)
        BlurKit.init(this)
        StyleTransfer.init()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }
}
