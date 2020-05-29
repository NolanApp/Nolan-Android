package soup.nolan.model

private const val MODE_SYSTEM = 1
private const val MODE_LIGHT = 2
private const val MODE_DARK = 3

enum class Appearance(val value: Int) {
    System(MODE_SYSTEM),
    Light(MODE_LIGHT),
    Dark(MODE_DARK);

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
