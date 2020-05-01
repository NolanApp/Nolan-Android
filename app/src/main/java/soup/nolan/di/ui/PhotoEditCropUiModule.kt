package soup.nolan.di.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import soup.nolan.di.scope.FragmentScope
import soup.nolan.ui.edit.crop.PhotoEditCropFragment

@Module
abstract class PhotoEditCropUiModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindEditCropFragment(): PhotoEditCropFragment
}
