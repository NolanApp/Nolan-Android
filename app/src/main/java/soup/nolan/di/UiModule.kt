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
            CameraAdsUiModule::class,
            PhotoEditUiModule::class,
            PhotoEditCropUiModule::class,
            ShareUiModule::class,
            SettingsUiModule::class,
            PurchaseUiModule::class,
            SystemUiModule::class
        ]
    )
    abstract fun bindNolanActivity(): NolanActivity
}
