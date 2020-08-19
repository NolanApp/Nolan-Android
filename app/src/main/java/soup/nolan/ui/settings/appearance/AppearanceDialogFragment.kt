package soup.nolan.ui.settings.appearance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import soup.nolan.R
import soup.nolan.databinding.AppearanceDialogBinding
import soup.nolan.model.Appearance
import soup.nolan.ui.base.BaseDialogFragment
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.NonNullObserver
import soup.nolan.ui.utils.setOnDebounceClickListener

@AndroidEntryPoint
class AppearanceDialogFragment : BaseDialogFragment(R.layout.appearance_dialog) {

    private val viewModel: SystemViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(AppearanceDialogBinding.bind(view)) {
            optionSystem.setOnDebounceClickListener {
                viewModel.onAppearanceChanged(Appearance.System)
                dismiss()
            }
            optionLight.setOnDebounceClickListener {
                viewModel.onAppearanceChanged(Appearance.Light)
                dismiss()
            }
            optionDark.setOnDebounceClickListener {
                viewModel.onAppearanceChanged(Appearance.Dark)
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
            viewModel.currentAppearance.observe(viewLifecycleOwner, NonNullObserver {
                val currentOptionId = when (it) {
                    Appearance.System -> R.id.option_system
                    Appearance.Light -> R.id.option_light
                    Appearance.Dark -> R.id.option_dark
                }
                optionsGroup.check(currentOptionId)
            })
        }
    }
}
