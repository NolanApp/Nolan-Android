package soup.nolan.ui.system

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.window.FoldingFeature
import androidx.window.WindowLayoutInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.model.Appearance
import soup.nolan.settings.AppSettings
import soup.nolan.ui.utils.postValueIfNew
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SystemViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _currentAppearance = MutableLiveData<Appearance>()
    val currentAppearance: LiveData<Appearance>
        get() = _currentAppearance

    private val _isHalfOpened = MutableLiveData<Boolean>()
    val isHalfOpened: LiveData<Boolean>
        get() = _isHalfOpened

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _currentAppearance.postValue(Appearance.of(appSettings.currentAppearance))
        }
    }

    fun onWindowLayoutInfoChanged(windowLayoutInfo: WindowLayoutInfo) {
        Timber.d("onWindowLayoutInfoChanged: $windowLayoutInfo")
        val foldingFeature = windowLayoutInfo.getFoldTypeOrNull()
        if (foldingFeature != null) {
            val isHalfOpened = foldingFeature.state == FoldingFeature.STATE_HALF_OPENED
            _isHalfOpened.postValueIfNew(isHalfOpened)
        }
    }

    private fun WindowLayoutInfo.getFoldTypeOrNull(): FoldingFeature? {
        return displayFeatures.firstOrNull {
            it is FoldingFeature && it.type == FoldingFeature.TYPE_FOLD
        } as? FoldingFeature
    }

    fun onAppearanceChanged(appearance: Appearance) {
        if (_currentAppearance.value == appearance) return
        _currentAppearance.value = appearance
        appSettings.currentAppearance = appearance.value
        AppCompatDelegate.setDefaultNightMode(appearance.nightMode)
    }
}
