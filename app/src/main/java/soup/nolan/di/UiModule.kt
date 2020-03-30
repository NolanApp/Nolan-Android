package soup.nolan.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import soup.nolan.di.scope.ActivityScope
import soup.nolan.di.ui.*
import soup.nolan.ui.NolanActivity

@Module
abstract class UiModule {

    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            SplashUiModule::class,
            CameraUiModule::class,
            PhotoEditUiModule::class,
            ShareUiModule::class,
            SettingsUiModule::class
        ]
    )
    abstract fun bindNolanActivity(): NolanActivity
}
