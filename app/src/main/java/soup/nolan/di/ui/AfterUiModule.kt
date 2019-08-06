package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.FragmentScope
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.after.AfterFragment
import soup.nolan.ui.after.AfterViewModel

@Module
abstract class AfterUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindAfterFragment(): AfterFragment

    @Binds
    @IntoMap
    @ViewModelKey(AfterViewModel::class)
    abstract fun bindAfterViewModel(viewModel: AfterViewModel): ViewModel
}
