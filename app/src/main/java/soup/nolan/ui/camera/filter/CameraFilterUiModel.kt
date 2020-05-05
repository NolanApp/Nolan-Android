package soup.nolan.ui.camera.filter

import androidx.annotation.DrawableRes
import soup.nolan.R
import soup.nolan.filter.stylize.StyleInput
import soup.nolan.model.CameraFilter

class CameraFilterUiModel(
    val list: List<CameraFilterItemUiModel>
)

class CameraFilterItemUiModel(
    val id: String,
    val style: StyleInput,
    @DrawableRes
    val thumbnailResId: Int
)

@DrawableRes
fun CameraFilter.getThumbnailResId(): Int {
    return when (id) {
        CameraFilter.A01.id -> R.drawable.style0
        CameraFilter.A02.id -> R.drawable.style1
        CameraFilter.A03.id -> R.drawable.style2
        CameraFilter.A04.id -> R.drawable.style3
        CameraFilter.A05.id -> R.drawable.style4
        CameraFilter.A06.id -> R.drawable.style5
        CameraFilter.A07.id -> R.drawable.style6
        CameraFilter.A08.id -> R.drawable.style7
        CameraFilter.A09.id -> R.drawable.style8
        CameraFilter.A10.id -> R.drawable.style9
        CameraFilter.A11.id -> R.drawable.style10
        CameraFilter.A12.id -> R.drawable.style11
        CameraFilter.A13.id -> R.drawable.style12
        CameraFilter.A14.id -> R.drawable.style13
        CameraFilter.A15.id -> R.drawable.style14
        CameraFilter.A16.id -> R.drawable.style15
        CameraFilter.A17.id -> R.drawable.style16
        CameraFilter.A18.id -> R.drawable.style17
        CameraFilter.A19.id -> R.drawable.style18
        CameraFilter.A20.id -> R.drawable.style19
        CameraFilter.A21.id -> R.drawable.style20
        CameraFilter.A22.id -> R.drawable.style21
        CameraFilter.A23.id -> R.drawable.style22
        CameraFilter.A24.id -> R.drawable.style23
        CameraFilter.A25.id -> R.drawable.style24
        CameraFilter.A26.id -> R.drawable.style25
        else -> throw IllegalArgumentException()
    }
}
