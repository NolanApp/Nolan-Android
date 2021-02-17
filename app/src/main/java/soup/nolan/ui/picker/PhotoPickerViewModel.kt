package soup.nolan.ui.picker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.data.GalleryRepository
import javax.inject.Inject

@HiltViewModel
class PhotoPickerViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _uiModel = MutableLiveData<List<PhotoPickerItemUiModel>>()
    val uiModel: LiveData<List<PhotoPickerItemUiModel>>
        get() = _uiModel

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.Default) {
            _uiModel.postValue(
                galleryRepository.getMediaList()
                    .map { PhotoPickerItemUiModel(it.uri) }
            )
        }
    }
}
