package soup.nolan.ui.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import soup.nolan.R

fun Context.startActivitySafely(intent: Intent) {
    if (isValid(intent)) {
        startActivity(intent)
    } else {
        toast(getString(R.string.toast_activity_not_found))
    }
}

private fun Context.isValid(intent: Intent): Boolean {
    return packageManager
        ?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        .isNullOrEmpty().not()
}

fun Context.executePlayStoreForApp(pkgName: String) {
    val googlePlayIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$pkgName")
    )
    if (isValid(googlePlayIntent)) {
        startActivity(googlePlayIntent)
        return
    }

    val browserIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=$pkgName")
    )
    if (isValid(browserIntent)) {
        startActivity(browserIntent)
        return
    }

    toast(getString(R.string.toast_activity_not_found))
}
