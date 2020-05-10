package soup.nolan.filter.stylize

import soup.nolan.stylize.common.StyleInput

data class LegacyStyleInput(
    private val style01: Float = 0f,
    private val style02: Float = 0f,
    private val style03: Float = 0f,
    private val style04: Float = 0f,
    private val style05: Float = 0f,
    private val style06: Float = 0f,
    private val style07: Float = 0f,
    private val style08: Float = 0f,
    private val style09: Float = 0f,
    private val style10: Float = 0f,
    private val style11: Float = 0f,
    private val style12: Float = 0f,
    private val style13: Float = 0f,
    private val style14: Float = 0f,
    private val style15: Float = 0f,
    private val style16: Float = 0f,
    private val style17: Float = 0f,
    private val style18: Float = 0f,
    private val style19: Float = 0f,
    private val style20: Float = 0f,
    private val style21: Float = 0f,
    private val style22: Float = 0f,
    private val style23: Float = 0f,
    private val style24: Float = 0f,
    private val style25: Float = 0f,
    private val style26: Float = 0f
) : StyleInput() {

    val count = 26

    fun toFloatArray(): FloatArray = floatArrayOf(
        style01, style02, style03, style04, style05, style06, style07, style08, style09, style10,
        style11, style12, style13, style14, style15, style16, style17, style18, style19, style20,
        style21, style22, style23, style24, style25, style26
    )
}
