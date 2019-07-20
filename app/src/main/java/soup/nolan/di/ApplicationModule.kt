package soup.nolan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import soup.nolan.NolanApplication

@Module
class ApplicationModule {

    @Provides
    fun provideContext(
        application: NolanApplication
    ): Context = application.applicationContext
}
