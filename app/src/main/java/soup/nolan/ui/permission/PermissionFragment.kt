package soup.nolan.ui.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import soup.nolan.R
import soup.nolan.databinding.PermissionBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast

@AndroidEntryPoint
class PermissionFragment : Fragment(R.layout.permission) {

    private val viewModel: PermissionViewModel by viewModels()

    private val requestPermissions =
        registerForActivityResult(RequestMultiplePermissions()) { result ->
            if (result.all { it.value }) {
                viewModel.onPermissionGranted()
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
        viewModel.navigationEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                PermissionUiEvent.GoToFilterEditor ->
                    findNavController().navigate(PermissionFragmentDirections.actionToFilterEditor())
                PermissionUiEvent.GoToCamera ->
                    findNavController().navigate(PermissionFragmentDirections.actionToCamera())
            }
        })
    }

    companion object {

        fun hasRequiredPermissions(context: Context): Boolean {
            return listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).any {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }
        }
    }
}
