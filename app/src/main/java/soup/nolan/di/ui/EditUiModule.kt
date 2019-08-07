package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.FragmentScope
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.edit.EditFragment
import soup.nolan.ui.edit.EditViewModel

@Module
abstract class EditUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindEditFragment(): EditFragment

    @Binds
    @IntoMap
    @ViewModelKey(EditViewModel::class)
    abstract fun bindEditViewModel(viewModel: EditViewModel): ViewModel
}
