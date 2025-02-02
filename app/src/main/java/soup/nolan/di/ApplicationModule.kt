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
import soup.nolan.ui.camera.filter.CameraFilterViewModelDelegate
import soup.nolan.ui.camera.filter.CameraFilterViewModelDelegateImpl
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
    fun provideLegacyStyleTransfer(
        @ApplicationContext context: Context
    ): LegacyStyleTransfer = LegacyStyleTransfer(context)

    @Singleton
    @Provides
    fun provideCameraFilterRepository(
    ): CameraFilterRepository = CameraFilterRepositoryImpl()

    @Singleton
    @Provides
    fun provideCameraFilterViewModelDelegate(
        @ApplicationContext context: Context,
        repository: CameraFilterRepository,
        imageStore: ImageStore,
        appSettings: AppSettings
    ): CameraFilterViewModelDelegate {
        return CameraFilterViewModelDelegateImpl(
            context,
            repository,
            imageStore,
            appSettings
        )
    }
}
