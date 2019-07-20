package soup.nolan.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import soup.nolan.NolanApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        UiModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<NolanApplication> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<NolanApplication>
}
