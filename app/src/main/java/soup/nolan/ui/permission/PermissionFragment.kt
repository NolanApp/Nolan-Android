package soup.nolan.ui.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import soup.nolan.Dependency
import soup.nolan.R
import soup.nolan.databinding.PermissionBinding
import soup.nolan.ui.permission.PermissionFragmentDirections.Companion.actionToFilterEditor
import soup.nolan.ui.permission.PermissionFragmentDirections.Companion.actionToCamera
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast

class PermissionFragment : Fragment(R.layout.permission) {

    private val requestPermissions =
        registerForActivityResult(RequestMultiplePermissions()) { result ->
            if (result.all { it.value }) {
                if (Dependency.appSettings.showFilterEditor) {
                    findNavController().navigate(actionToFilterEditor())
                } else {
                    findNavController().navigate(actionToCamera())
                }
            } else {
                toast(R.string.camera_error_permission)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PermissionBinding.bind(view).apply {
            allowButton.setOnDebounceClickListener {
                val permissionList = mutableListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (cameraSwitch.isChecked) {
                    permissionList.add(Manifest.permission.CAMERA)
                }
                requestPermissions.launch(permissionList.toTypedArray())
            }
        }
    }

    companion object {

        fun showPermission(context: Context): Boolean {
            return listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).any {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }
        }
    }
}
