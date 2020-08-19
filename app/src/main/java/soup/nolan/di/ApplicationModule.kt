package soup.nolan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import soup.nolan.data.*
import soup.nolan.factory.*
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.settings.AppSettings
import soup.nolan.settings.AppSettingsImpl
import soup.nolan.work.FilterThumbnailWorker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Singleton
    @Provides
    fun provideAppSettings(
        @ApplicationContext context: Context
    ): AppSettings = AppSettingsImpl(context)

    @Singleton
    @Provides
    fun provideImageFactory(
        @ApplicationContext context: Context
    ): ImageFactory = ImageFactoryImpl(context)

    @Singleton
    @Provides
    fun provideShareUriFactory(
        @ApplicationContext context: Context,
        imageFactory: ImageFactory
    ): ShareUriFactory = ShareUriFactoryImpl(context, imageFactory)

    @Singleton
    @Provides
    fun provideImageStore(
        @ApplicationContext context: Context
    ): ImageStore = ImageStoreImpl(context)

    @Singleton
    @Provides
    fun providePlayRepository(
        @ApplicationContext context: Context
    ): PlayRepository = PlayRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideShareRepository(
        @ApplicationContext context: Context
    ): ShareRepository = ShareRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideGalleryRepository(
        @ApplicationContext context: Context
    ): GalleryRepository = GalleryRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideCameraFilterRepository(
        @ApplicationContext context: Context,
        dataSource: FilterThumbnailWorker.DataSource
    ): CameraFilterRepository = CameraFilterRepositoryImpl(context, dataSource)

    @Singleton
    @Provides
    fun provideLegacyStyleTransfer(
        @ApplicationContext context: Context
    ): LegacyStyleTransfer = LegacyStyleTransfer(context)

    @Singleton
    @Provides
    fun provideFilterThumbnailWorkerDataSource(
        @ApplicationContext context: Context,
        imageStore: ImageStore
    ): FilterThumbnailWorker.DataSource = FilterThumbnailWorker.DataSource(context, imageStore)
}
