package soup.nolan.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import soup.nolan.di.scope.ActivityScope
import soup.nolan.di.ui.AfterUiModule
import soup.nolan.di.ui.CameraUiModule
import soup.nolan.ui.NolanActivity

@Module
abstract class UiModule {

    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            CameraUiModule::class,
            AfterUiModule::class
        ]
    )
    abstract fun bindNolanActivity(): NolanActivity
}
