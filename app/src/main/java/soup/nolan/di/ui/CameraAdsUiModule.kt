package soup.nolan.di.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import soup.nolan.di.scope.FragmentScope
import soup.nolan.ui.camera.ads.CameraAdsDialogFragment

@Module
abstract class CameraAdsUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindCameraAdsDialogFragment(): CameraAdsDialogFragment
}
