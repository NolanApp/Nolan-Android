package soup.nolan

import android.app.Application
import soup.nolan.data.*
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.settings.AppSettings
import soup.nolan.settings.AppSettingsImpl
import soup.nolan.ui.share.ShareUriFactory
import soup.nolan.ui.utils.ImageFactory

class Dependency(application: Application) {

    private val appSettings: AppSettings
    private val imageFactory: ImageFactory
    private val shareUriFactory: ShareUriFactory
    private val styleTransfer: LegacyStyleTransfer
    private val playRepository: PlayRepository
    private val shareRepository: ShareRepository
    private val galleryRepository: GalleryRepository

    init {
        val appContext = application.applicationContext
        appSettings = AppSettingsImpl(appContext)
        imageFactory = ImageFactory(appContext)
        shareUriFactory = ShareUriFactory(appContext, imageFactory)
        styleTransfer = LegacyStyleTransfer(appContext)
        playRepository = PlayRepositoryImpl(appContext)
        shareRepository = ShareRepositoryImpl(appContext)
        galleryRepository = GalleryRepositoryImpl(appContext)
    }

    companion object {

        val appSettings: AppSettings
            get() = NolanApplication.dependency.appSettings

        val imageFactory: ImageFactory
            get() = NolanApplication.dependency.imageFactory

        val shareUriFactory: ShareUriFactory
            get() = NolanApplication.dependency.shareUriFactory

        val styleTransfer: LegacyStyleTransfer
            get() = NolanApplication.dependency.styleTransfer

        val playRepository: PlayRepository
            get() = NolanApplication.dependency.playRepository

        val shareRepository: ShareRepository
            get() = NolanApplication.dependency.shareRepository

        val galleryRepository: GalleryRepository
            get() = NolanApplication.dependency.galleryRepository
    }
}
