package soup.nolan.ui.onboarding

import androidx.lifecycle.ViewModel
import soup.nolan.ui.EventLiveData
import soup.nolan.ui.MutableEventLiveData

class OnBoardingViewModel : ViewModel() {

    private val _startEvent = MutableEventLiveData<Unit>()
    val uiEvent: EventLiveData<Unit>
        get() = _startEvent

    fun onClickStart() {
        _startEvent.event = Unit
    }
}
