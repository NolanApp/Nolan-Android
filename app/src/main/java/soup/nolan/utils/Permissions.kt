package soup.nolan.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

private val REQUIRED_PERMISSIONS = listOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

fun Context.hasRequiredPermissions(): Boolean {
    return REQUIRED_PERMISSIONS.all(::isGranted)
}

fun Context.hasCameraPermission(): Boolean {
    return isGranted(Manifest.permission.CAMERA)
}

private fun Context.isGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
