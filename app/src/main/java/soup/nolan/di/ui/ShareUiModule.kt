package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.share.ShareViewModel

@Module
abstract class ShareUiModule {

    @Binds
    @IntoMap
    @ViewModelKey(ShareViewModel::class)
    abstract fun bindShareViewModel(viewModel: ShareViewModel): ViewModel
}
