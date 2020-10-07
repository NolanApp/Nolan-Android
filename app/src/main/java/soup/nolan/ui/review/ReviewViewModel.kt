package soup.nolan.ui.review

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.data.PlayRepository
import soup.nolan.settings.AppSettings

class ReviewViewModel @ViewModelInject constructor(
    private val repository: PlayRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    private var reviewInfo: ReviewInfo? = null

    init {
        if (canRequestReview()) {
            viewModelScope.launch(Dispatchers.IO) {
                reviewInfo = runCatching { repository.requestReview() }.getOrNull()
            }
        }
    }

    fun obtainReviewInfo(): ReviewInfo? {
        return reviewInfo.takeIf { canRequestReview() }
    }

    fun notifyAskedForReview() {
        appSettings.alreadyAskedForReview = true
    }

    private fun canRequestReview(): Boolean {
        val beginner = appSettings.photoEditCount < 5
        return !(beginner || appSettings.alreadyAskedForReview)
    }
}
