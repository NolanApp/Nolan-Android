package soup.nolan.settings

import android.content.Context
import soup.nolan.model.CameraFilter

interface AppSettings {
    var lensFacingFront: Boolean
    var lastFilterId: String
    var gallerySelectableCount: Int
}

class AppSettingsImpl(context: Context) : AppSettings {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override var lensFacingFront: Boolean by BooleanPreference(prefs, "lens_facing_front", true)
    override var lastFilterId: String by StringPreference(prefs, "last_filter_id", CameraFilter.A25.id)
    override var gallerySelectableCount: Int by IntPreference(prefs, "gallery_selectable_count", 10)
}
