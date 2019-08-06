package soup.nolan.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import soup.nolan.ui.utils.activityViewModelProvider
import soup.nolan.ui.utils.lazyFast
import soup.nolan.ui.utils.parentViewModelProvider
import soup.nolan.ui.utils.viewModelProvider
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected inline fun <reified VM : ViewModel> activityViewModel(): Lazy<VM> =
        lazyFast { activityViewModelProvider<VM>(viewModelFactory) }

    protected inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> =
        lazyFast { viewModelProvider<VM>(viewModelFactory) }

    protected inline fun <reified VM : ViewModel> parentViewModel(): Lazy<VM> =
        lazyFast { parentViewModelProvider<VM>(viewModelFactory) }
}
