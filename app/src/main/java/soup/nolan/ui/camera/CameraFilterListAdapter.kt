package soup.nolan.ui.camera

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import soup.nolan.R
import soup.nolan.ui.databinding.DataBindingAdapter
import soup.nolan.ui.databinding.DataBindingViewHolder
import soup.nolan.ui.utils.setOnDebounceClickListener

class CameraFilterListAdapter(
    private val clickListener: (CameraFilterUiModel) -> Unit
) : DataBindingAdapter<CameraFilterUiModel>() {

    override fun getLayoutResId(viewType: Int): Int {
        return R.layout.camera_item_filter
    }

    override fun createViewHolder(binding: ViewDataBinding): DataBindingViewHolder<CameraFilterUiModel> {
        return super.createViewHolder(binding).apply {
            itemView.setOnDebounceClickListener {
                getItem(adapterPosition)?.run(clickListener)
            }
        }
    }
}

@BindingAdapter("cameraFilter")
fun setCameraFilter(view: ImageView, uiModel: CameraFilterUiModel?) {
    if (uiModel == null) {
        view.setImageDrawable(null)
    } else {
        view.setImageResource(uiModel.thumbnailResId)
    }
}
