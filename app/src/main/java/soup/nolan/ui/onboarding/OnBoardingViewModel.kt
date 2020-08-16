package soup.nolan.ui.onboarding

import androidx.lifecycle.ViewModel
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData

class OnBoardingViewModel : ViewModel() {

    private val _allowEvent = MutableEventLiveData<Unit>()
    val allowEvent: EventLiveData<Unit>
        get() = _allowEvent

    fun onClickAllow() {
        _allowEvent.event = Unit
    }
}
