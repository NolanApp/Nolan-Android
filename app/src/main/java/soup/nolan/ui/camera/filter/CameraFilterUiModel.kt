package soup.nolan.ui.camera.filter

import androidx.annotation.DrawableRes
import soup.nolan.R
import soup.nolan.model.CameraFilter

class CameraFilterUiModel(
    val list: List<CameraFilterItemUiModel>
)

data class CameraFilterItemUiModel(
    val filter: CameraFilter
) {
    val id: String
        get() = filter.id

    @DrawableRes
    val thumbnailResId: Int = when (filter.id) {
        CameraFilter.OR.id -> R.drawable.original
        CameraFilter.A01.id -> R.drawable.a01
        CameraFilter.A02.id -> R.drawable.a02
        CameraFilter.A03.id -> R.drawable.a03
        CameraFilter.A04.id -> R.drawable.a04
        CameraFilter.A05.id -> R.drawable.a05
        CameraFilter.A06.id -> R.drawable.a06
        CameraFilter.A07.id -> R.drawable.a07
        CameraFilter.A08.id -> R.drawable.a08
        CameraFilter.A09.id -> R.drawable.a09
        CameraFilter.A10.id -> R.drawable.a10
        CameraFilter.A11.id -> R.drawable.a11
        CameraFilter.A12.id -> R.drawable.a12
        CameraFilter.A13.id -> R.drawable.a13
        CameraFilter.A14.id -> R.drawable.a14
        CameraFilter.A15.id -> R.drawable.a15
        CameraFilter.A16.id -> R.drawable.a16
        CameraFilter.A17.id -> R.drawable.a16
        CameraFilter.A18.id -> R.drawable.a18
        CameraFilter.A19.id -> R.drawable.a19
        CameraFilter.A20.id -> R.drawable.a20
        CameraFilter.A21.id -> R.drawable.a21
        CameraFilter.A22.id -> R.drawable.a22
        CameraFilter.A23.id -> R.drawable.a23
        CameraFilter.A24.id -> R.drawable.a24
        CameraFilter.A25.id -> R.drawable.a25
        CameraFilter.A26.id -> R.drawable.a26
        else -> throw IllegalArgumentException("CameraFilter(${filter.id}) is invalid.")
    }
}
