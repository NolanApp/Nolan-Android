package soup.nolan.ui.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * Utility class to handle icon treatments (e.g., shadow generation) for the Launcher icons.
 */
object LauncherIcons {

    fun getAppIcon(context: Context, packageName: String): Drawable? {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationIcon(appInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getAppInfo(context: Context, packageName: String, intent: Intent): AppInfo? {
        val pm = context.packageManager
        val icon = pm.getIcon(intent) ?: pm.getIcon(packageName)
        return if (icon != null) {
            AppInfo(icon, packageName)
        } else {
            null
        }
    }

    private fun PackageManager.getIcon(intent: Intent): Drawable? {
        return try {
            getActivityIcon(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun PackageManager.getIcon(packageName: String): Drawable? {
        return try {
            getApplicationIcon(getApplicationInfo(packageName, 0))
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun PackageManager.getLabel(intent: Intent): String? {
        return try {
            val component = intent.component
            if (component != null) {
                getActivityInfo(component, 0).name
            } else {
                null
            }
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun PackageManager.getLabel(packageName: String): String? {
        return try {
            getApplicationInfo(packageName, 0).name
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}

data class AppInfo(
    val icon: Drawable,
    val label: String
)
