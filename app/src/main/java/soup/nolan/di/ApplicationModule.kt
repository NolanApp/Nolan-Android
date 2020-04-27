package soup.nolan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import soup.nolan.NolanApplication
import soup.nolan.ads.AdManager
import soup.nolan.ads.AdManagerImpl
import soup.nolan.settings.AppSettings
import soup.nolan.settings.AppSettingsImpl
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    fun provideContext(
        application: NolanApplication
    ): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideAdManager(
        context: Context
    ): AdManager = AdManagerImpl(context)

    @Singleton
    @Provides
    fun provideAppSettings(
        context: Context
    ): AppSettings = AppSettingsImpl(context)
}
