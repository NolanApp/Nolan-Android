package soup.nolan.model

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
