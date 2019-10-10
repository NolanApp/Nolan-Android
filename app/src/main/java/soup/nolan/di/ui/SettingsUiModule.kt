package soup.nolan.di.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import soup.nolan.di.scope.FragmentScope
import soup.nolan.di.scope.ViewModelKey
import soup.nolan.ui.settings.SettingsFragment
import soup.nolan.ui.settings.SettingsViewModel

@Module
abstract class SettingsUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}
