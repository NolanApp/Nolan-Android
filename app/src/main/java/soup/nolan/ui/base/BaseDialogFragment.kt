package soup.nolan.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import dagger.android.support.DaggerAppCompatDialogFragment
import soup.nolan.R
import javax.inject.Inject

abstract class BaseDialogFragment : DaggerAppCompatDialogFragment {

    override fun getTheme(): Int {
        return R.style.Theme_Nolan_Dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, theme)
    }

    @LayoutRes
    private var contentLayoutId = 0

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super() {
        this.contentLayoutId = contentLayoutId
    }

    @MainThread
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (contentLayoutId != 0) {
            return inflater.inflate(contentLayoutId, container, false)
        }
        return null
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected inline fun <reified VM : ViewModel> activityViewModel(): Lazy<VM> {
        return activityViewModels { viewModelFactory }
    }

    protected inline fun <reified VM : ViewModel> viewModel(): Lazy<VM> {
        return viewModels { viewModelFactory }
    }

    protected inline fun <reified VM : ViewModel> parentViewModel(): Lazy<VM> {
        return requireParentFragment().viewModels { viewModelFactory }
    }

    protected inline fun <reified VM : ViewModel> navGraphViewModels(@IdRes navGraphId: Int): Lazy<VM> {
        return navGraphViewModels(navGraphId) { viewModelFactory }
    }
}