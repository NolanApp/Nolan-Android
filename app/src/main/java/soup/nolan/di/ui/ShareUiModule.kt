package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.FragmentScope
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.share.ShareFragment
import soup.nolan.ui.share.ShareViewModel

@Module
abstract class ShareUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindShareFragment(): ShareFragment

    @Binds
    @IntoMap
    @ViewModelKey(ShareViewModel::class)
    abstract fun bindShareViewModel(viewModel: ShareViewModel): ViewModel
}
