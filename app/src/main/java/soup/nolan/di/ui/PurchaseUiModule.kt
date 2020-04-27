package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.purchase.PurchaseViewModel

@Module
abstract class PurchaseUiModule {

    @Binds
    @IntoMap
    @ViewModelKey(PurchaseViewModel::class)
    abstract fun bindPurchaseViewModel(viewModel: PurchaseViewModel): ViewModel
}
