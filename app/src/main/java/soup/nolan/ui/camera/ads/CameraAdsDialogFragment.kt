package soup.nolan.ui.camera.ads

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import soup.nolan.R
import soup.nolan.databinding.CameraAdsDialogBinding
import soup.nolan.ui.ResultContract
import soup.nolan.ui.base.BaseDialogFragment
import soup.nolan.ui.utils.setOnDebounceClickListener

class CameraAdsDialogFragment : BaseDialogFragment(R.layout.camera_ads_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(CameraAdsDialogBinding.bind(view)) {
            confirmButton.setOnDebounceClickListener {
                setFragmentResult(
                    ResultContract.CAMERA,
                    bundleOf(ResultContract.CAMERA_EXTRA_SHOW_ADS to true)
                )
                dismiss()
            }
        }
    }
}
