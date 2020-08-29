package soup.nolan.settings

import android.content.Context
import soup.nolan.model.Appearance

interface AppSettings {
    var filterThumbnailsGenerated: Boolean
    var showOnBoarding: Boolean
    var showFilterEditor: Boolean

    var lensFacingFront: Boolean
    var lastFilterId: String?
    var showWatermark: Boolean
    var currentAppearance: Int
    var noAds: Boolean

    var photoEditCount: Int
    var alreadyAskedForReview: Boolean
    var lastAskedReviewTimeMs: Long
}

class AppSettingsImpl(context: Context) : AppSettings {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override var filterThumbnailsGenerated: Boolean
            by BooleanPreference(prefs, "filter_thumbnails_generated", false)
    override var showOnBoarding: Boolean
            by BooleanPreference(prefs, "show_onboarding", true)
    override var showFilterEditor: Boolean
            by BooleanPreference(prefs, "show_filter_editor", true)

    override var lensFacingFront: Boolean
            by BooleanPreference(prefs, "lens_facing_front", true)
    override var lastFilterId: String?
            by NullableStringPreference(prefs, "last_filter_id", null)
    override var showWatermark: Boolean
            by BooleanPreference(prefs, "show_watermark", true)
    override var currentAppearance: Int
            by IntPreference(prefs, "current_appearance", Appearance.System.value)
    override var noAds: Boolean
            by BooleanPreference(prefs, "no_ads", false)

    override var photoEditCount: Int
            by IntPreference(prefs, "photo_edit_count", 0)
    override var alreadyAskedForReview: Boolean
            by BooleanPreference(prefs, "already_asked_for_review", true)
    override var lastAskedReviewTimeMs: Long
            by LongPreference(prefs, "last_asked_review_time_ms", 0L)
}
