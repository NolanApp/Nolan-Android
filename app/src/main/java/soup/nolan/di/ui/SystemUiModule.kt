package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.system.SystemViewModel

@Module
abstract class SystemUiModule {

    @Binds
    @IntoMap
    @ViewModelKey(SystemViewModel::class)
    abstract fun bindSystemViewModel(viewModel: SystemViewModel): ViewModel
}
