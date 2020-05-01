package soup.nolan.ui.utils

import android.content.Context
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
}
