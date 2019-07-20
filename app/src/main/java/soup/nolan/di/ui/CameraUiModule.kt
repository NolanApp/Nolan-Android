package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.FragmentScope
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.camera.CameraFragment
import soup.nolan.ui.camera.CameraViewModel

@Module
abstract class CameraUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindCameraFragment(): CameraFragment

    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindBarcodeDetectViewModel(viewModel: CameraViewModel): ViewModel
}
