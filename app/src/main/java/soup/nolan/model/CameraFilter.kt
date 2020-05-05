package soup.nolan.model

import soup.nolan.filter.stylize.StyleInput

open class CameraFilter(val id: String, val input: StyleInput) {

    object A01 : CameraFilter(id = "A01", input = StyleInput(style01 = 1f))
    object A02 : CameraFilter(id = "A02", input = StyleInput(style02 = 1f))
    object A03 : CameraFilter(id = "A03", input = StyleInput(style03 = 1f))
    object A04 : CameraFilter(id = "A04", input = StyleInput(style04 = 1f))
    object A05 : CameraFilter(id = "A05", input = StyleInput(style05 = 1f))
    object A06 : CameraFilter(id = "A06", input = StyleInput(style06 = 1f))
    object A07 : CameraFilter(id = "A07", input = StyleInput(style07 = 1f))
    object A08 : CameraFilter(id = "A08", input = StyleInput(style08 = 1f))
    object A09 : CameraFilter(id = "A09", input = StyleInput(style09 = 1f))
    object A10 : CameraFilter(id = "A10", input = StyleInput(style10 = 1f))
    object A11 : CameraFilter(id = "A11", input = StyleInput(style11 = 1f))
    object A12 : CameraFilter(id = "A12", input = StyleInput(style12 = 1f))
    object A13 : CameraFilter(id = "A13", input = StyleInput(style13 = 1f))
    object A14 : CameraFilter(id = "A14", input = StyleInput(style14 = 1f))
    object A15 : CameraFilter(id = "A15", input = StyleInput(style15 = 1f))
    object A16 : CameraFilter(id = "A16", input = StyleInput(style16 = 1f))
    object A17 : CameraFilter(id = "A17", input = StyleInput(style17 = 1f))
    object A18 : CameraFilter(id = "A18", input = StyleInput(style18 = 1f))
    object A19 : CameraFilter(id = "A19", input = StyleInput(style19 = 1f))
    object A20 : CameraFilter(id = "A20", input = StyleInput(style20 = 1f))
    object A21 : CameraFilter(id = "A21", input = StyleInput(style21 = 1f))
    object A22 : CameraFilter(id = "A22", input = StyleInput(style22 = 1f))
    object A23 : CameraFilter(id = "A23", input = StyleInput(style23 = 1f))
    object A24 : CameraFilter(id = "A24", input = StyleInput(style24 = 1f))
    object A25 : CameraFilter(id = "A25", input = StyleInput(style25 = 1f))
    object A26 : CameraFilter(id = "A26", input = StyleInput(style26 = 1f))

    companion object {

        private val PRE_DEFINED_FILTER_LIST = listOf(
            A01, A02, A03, A04, A05, A06, A07, A08, A09, A10,
            A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
            A21, A22, A23, A24, A25, A26
        )

        fun all(): List<CameraFilter> {
            return PRE_DEFINED_FILTER_LIST
        }
    }
}
