package soup.nolan.settings

import android.content.Context

interface AppSettings {
    var lensFacingFront: Boolean

    //TODO:
    var noAds: Boolean
    var buyCoffee01: Boolean
    var buyCoffee02: Boolean
    var buyCoffee03: Boolean
    var buyCoffee04: Boolean
    var buyCoffee05: Boolean
}

class AppSettingsImpl(context: Context) : AppSettings {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override var lensFacingFront: Boolean by BooleanPreference(prefs, "lens_facing_front", true)

    override var noAds: Boolean by BooleanPreference(prefs, "no_ads", false)
    override var buyCoffee01: Boolean by BooleanPreference(prefs, "buy_coffee_01", false)
    override var buyCoffee02: Boolean by BooleanPreference(prefs, "buy_coffee_02", false)
    override var buyCoffee03: Boolean by BooleanPreference(prefs, "buy_coffee_03", false)
    override var buyCoffee04: Boolean by BooleanPreference(prefs, "buy_coffee_04", false)
    override var buyCoffee05: Boolean by BooleanPreference(prefs, "buy_coffee_05", false)
}
