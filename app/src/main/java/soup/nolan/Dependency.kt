package soup.nolan

import android.app.Application
import soup.nolan.data.PlayRepository
import soup.nolan.data.PlayRepositoryImpl
import soup.nolan.data.ShareRepository
import soup.nolan.data.ShareRepositoryImpl
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.settings.AppSettings
import soup.nolan.settings.AppSettingsImpl
import soup.nolan.ui.utils.ImageFactory

class Dependency(application: Application) {

    private val appSettings: AppSettings
    private val imageFactory: ImageFactory
    private val styleTransfer: LegacyStyleTransfer
    private val playRepository: PlayRepository
    private val shareRepository: ShareRepository

    init {
        val appContext = application.applicationContext
        appSettings = AppSettingsImpl(appContext)
        imageFactory = ImageFactory(appContext)
        styleTransfer = LegacyStyleTransfer(appContext)
        playRepository = PlayRepositoryImpl(appContext)
        shareRepository = ShareRepositoryImpl(appContext)
    }

    companion object {

        val appSettings: AppSettings
            get() = NolanApplication.dependency.appSettings

        val imageFactory: ImageFactory
            get() = NolanApplication.dependency.imageFactory

        val styleTransfer: LegacyStyleTransfer
            get() = NolanApplication.dependency.styleTransfer

        val playRepository: PlayRepository
            get() = NolanApplication.dependency.playRepository

        val shareRepository: ShareRepository
            get() = NolanApplication.dependency.shareRepository
    }
}
