package soup.nolan.ui.onboarding

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import soup.nolan.R
import soup.nolan.databinding.OnBoardingPage3Binding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.utils.toast

@AndroidEntryPoint
class OnBoardingPage3Fragment : Fragment(R.layout.on_boarding_page3) {

    private val viewModel: OnBoardingViewModel by activityViewModels()

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.all { it.value }) {
                viewModel.onPermissionGranted()
            } else {
                toast(R.string.permission_error_message)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OnBoardingPage3Binding.bind(view).apply {
            viewModel.allowEvent.observe(viewLifecycleOwner, EventObserver {
                val permissionList = mutableListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (cameraSwitch.isChecked) {
                    permissionList.add(Manifest.permission.CAMERA)
                }
                requestPermissions.launch(permissionList.toTypedArray())
            })
        }
    }
}
