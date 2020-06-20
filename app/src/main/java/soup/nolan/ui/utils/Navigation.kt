package soup.nolan.ui.utils

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment

fun FragmentActivity.findNavHostFragment(@IdRes viewId: Int): NavHostFragment {
    return supportFragmentManager.findFragmentById(viewId) as NavHostFragment
}
