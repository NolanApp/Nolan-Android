package soup.nolan.model

import androidx.appcompat.app.AppCompatDelegate.*

private const val MODE_SYSTEM = 1
private const val MODE_LIGHT = 2
private const val MODE_DARK = 3

enum class Appearance(val value: Int, val nightMode: Int) {
    System(MODE_SYSTEM, MODE_NIGHT_FOLLOW_SYSTEM),
    Light(MODE_LIGHT, MODE_NIGHT_NO),
    Dark(MODE_DARK, MODE_NIGHT_YES);

    companion object {

        fun of(value: Int): Appearance {
            return when (value) {
                MODE_SYSTEM -> System
                MODE_LIGHT -> Light
                MODE_DARK -> Dark
                else -> System
            }
        }
    }
}
