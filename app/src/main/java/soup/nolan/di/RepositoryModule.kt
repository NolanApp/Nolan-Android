package soup.nolan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import soup.nolan.data.PlayRepository
import soup.nolan.data.PlayRepositoryImpl
import soup.nolan.data.ShareRepository
import soup.nolan.data.ShareRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providePlayRepository(context: Context): PlayRepository {
        return PlayRepositoryImpl(context)
    }

    @Singleton
    @Provides
    fun provideShareRepository(context: Context): ShareRepository {
        return ShareRepositoryImpl(context)
    }
}
