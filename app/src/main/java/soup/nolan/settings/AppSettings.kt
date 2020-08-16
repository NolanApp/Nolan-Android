package soup.nolan.settings

import android.content.Context
import soup.nolan.model.Appearance
import soup.nolan.model.CameraFilter

interface AppSettings {
    var showOnBoarding: Boolean
    var lensFacingFront: Boolean
    var lastFilterId: String
    var showWatermark: Boolean
    var currentAppearance: Int
    var noAds: Boolean
}

class AppSettingsImpl(context: Context) : AppSettings {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override var showOnBoarding: Boolean by BooleanPreference(prefs, "show_onboarding", true)
    override var lensFacingFront: Boolean by BooleanPreference(prefs, "lens_facing_front", true)
    override var lastFilterId: String by StringPreference(prefs, "last_filter_id", CameraFilter.A25.id)
    override var showWatermark: Boolean by BooleanPreference(prefs, "show_watermark", true)
    override var currentAppearance: Int by IntPreference(prefs, "current_appearance", Appearance.System.value)
    override var noAds: Boolean by BooleanPreference(prefs, "no_ads", false)
}
