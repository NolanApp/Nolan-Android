package soup.nolan.settings

import android.content.Context
import soup.nolan.model.Appearance
import soup.nolan.model.CameraFilter

interface AppSettings {
    var lensFacingFront: Boolean
    var lastFilterId: String
    var showWatermark: Boolean
    var currentAppearance: Int
}

class AppSettingsImpl(context: Context) : AppSettings {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override var lensFacingFront: Boolean by BooleanPreference(prefs, "lens_facing_front", true)
    override var lastFilterId: String by StringPreference(prefs, "last_filter_id", CameraFilter.A25.id)
    override var showWatermark: Boolean by BooleanPreference(prefs, "show_watermark", true)
    override var currentAppearance: Int by IntPreference(prefs, "current_appearance", Appearance.System.value)
}
