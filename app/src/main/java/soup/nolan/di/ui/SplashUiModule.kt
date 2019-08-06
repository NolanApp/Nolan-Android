package soup.nolan.di.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import soup.nolan.di.scope.FragmentScope
import soup.nolan.ui.splash.SplashFragment

@Module
abstract class SplashUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSplashFragment(): SplashFragment
}
