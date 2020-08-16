package soup.nolan.model

import androidx.annotation.DrawableRes
import soup.nolan.R
import soup.nolan.filter.stylize.LegacyStyleInput
import soup.nolan.stylize.common.NoStyleInput
import soup.nolan.stylize.common.StyleInput

open class CameraFilter(val id: String, val input: StyleInput) {

    object OR  : CameraFilter(id = "OR", input =  NoStyleInput)
    object A01 : CameraFilter(id = "A01", input = LegacyStyleInput(style01 = 1f))
    object A02 : CameraFilter(id = "A02", input = LegacyStyleInput(style02 = 1f))
    object A03 : CameraFilter(id = "A03", input = LegacyStyleInput(style03 = 1f))
    object A04 : CameraFilter(id = "A04", input = LegacyStyleInput(style04 = 1f))
    object A05 : CameraFilter(id = "A05", input = LegacyStyleInput(style05 = 1f))
    object A06 : CameraFilter(id = "A06", input = LegacyStyleInput(style06 = 1f))
    object A07 : CameraFilter(id = "A07", input = LegacyStyleInput(style07 = 1f))
    object A08 : CameraFilter(id = "A08", input = LegacyStyleInput(style08 = 1f))
    object A09 : CameraFilter(id = "A09", input = LegacyStyleInput(style09 = 1f))
    object A10 : CameraFilter(id = "A10", input = LegacyStyleInput(style10 = 1f))
    object A11 : CameraFilter(id = "A11", input = LegacyStyleInput(style11 = 1f))
    object A12 : CameraFilter(id = "A12", input = LegacyStyleInput(style12 = 1f))
    object A13 : CameraFilter(id = "A13", input = LegacyStyleInput(style13 = 1f))
    object A14 : CameraFilter(id = "A14", input = LegacyStyleInput(style14 = 1f))
    object A15 : CameraFilter(id = "A15", input = LegacyStyleInput(style15 = 1f))
    object A16 : CameraFilter(id = "A16", input = LegacyStyleInput(style16 = 1f))
    object A17 : CameraFilter(id = "A17", input = LegacyStyleInput(style17 = 1f))
    object A18 : CameraFilter(id = "A18", input = LegacyStyleInput(style18 = 1f))
    object A19 : CameraFilter(id = "A19", input = LegacyStyleInput(style19 = 1f))
    object A20 : CameraFilter(id = "A20", input = LegacyStyleInput(style20 = 1f))
    object A21 : CameraFilter(id = "A21", input = LegacyStyleInput(style21 = 1f))
    object A22 : CameraFilter(id = "A22", input = LegacyStyleInput(style22 = 1f))
    object A23 : CameraFilter(id = "A23", input = LegacyStyleInput(style23 = 1f))
    object A24 : CameraFilter(id = "A24", input = LegacyStyleInput(style24 = 1f))
    object A25 : CameraFilter(id = "A25", input = LegacyStyleInput(style25 = 1f))
    object A26 : CameraFilter(id = "A26", input = LegacyStyleInput(style26 = 1f))

    companion object {

        fun all(): List<CameraFilter> {
            return listOf(
                OR,
                A01, A02, A03, A04, A05, A06, A07, A08, A09, A10,
                A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
                A21, A22, A23, A24, A25, A26
            )
        }
    }
}

val CameraFilter.thumbnailResId: Int
    @DrawableRes
    get() = when (id) {
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
        CameraFilter.A17.id -> R.drawable.a17
        CameraFilter.A18.id -> R.drawable.a18
        CameraFilter.A19.id -> R.drawable.a19
        CameraFilter.A20.id -> R.drawable.a20
        CameraFilter.A21.id -> R.drawable.a21
        CameraFilter.A22.id -> R.drawable.a22
        CameraFilter.A23.id -> R.drawable.a23
        CameraFilter.A24.id -> R.drawable.a24
        CameraFilter.A25.id -> R.drawable.a25
        CameraFilter.A26.id -> R.drawable.a26
        else -> throw IllegalArgumentException("CameraFilter($id) is invalid.")
    }
