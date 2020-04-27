package soup.nolan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import soup.nolan.data.PlayRepository
import soup.nolan.data.PlayRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providePlayRepository(context: Context): PlayRepository {
        return PlayRepositoryImpl(context)
    }
}
