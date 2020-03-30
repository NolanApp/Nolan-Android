package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.FragmentScope
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.edit.PhotoEditFragment
import soup.nolan.ui.edit.PhotoEditViewModel

@Module
abstract class PhotoEditUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindEditFragment(): PhotoEditFragment

    @Binds
    @IntoMap
    @ViewModelKey(PhotoEditViewModel::class)
    abstract fun bindEditViewModel(viewModel: PhotoEditViewModel): ViewModel
}
