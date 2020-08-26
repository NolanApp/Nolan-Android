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

    private var alreadyAskedForReview: Boolean = false
    private var reviewInfo: ReviewInfo? = null
    private var inProgress: Boolean = false

    fun prepareReviewInfo() {
        if (!alreadyAskedForReview && reviewInfo == null && inProgress.not()) {
            inProgress = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    reviewInfo = repository.requestReview()
                } finally {
                    inProgress = false
                }
            }
        }
    }

    fun obtainReviewInfo(): ReviewInfo? {
        return reviewInfo.also {
            reviewInfo = null
        }
    }

    fun notifyAskedForReview() {
        alreadyAskedForReview = true
    }
}
