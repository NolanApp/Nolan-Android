package soup.nolan.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.databinding.SettingsBinding
import soup.nolan.model.Appearance
import soup.nolan.ui.EventObserver
import soup.nolan.ui.purchase.PurchaseViewModel
import soup.nolan.ui.settings.SettingsFragmentDirections.Companion.actionToAppearance
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.*

class SettingsFragment : Fragment(R.layout.settings) {

    private var binding: SettingsBinding by autoCleared { adView.destroy() }
    private val viewModel: SettingsViewModel by viewModels()
    private val systemViewModel: SystemViewModel by activityViewModels()
    private val purchaseViewModel: PurchaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SettingsBinding.bind(view)) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            appearanceButton.setOnDebounceClickListener {
                findNavController().navigate(actionToAppearance())
            }
            watermarkSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onShowWatermarkChecked(isChecked)
            }
            reviewButton.setOnDebounceClickListener {
                it.context.executePlayStoreForApp(BuildConfig.APPLICATION_ID)
            }
            bugReportButton.setOnDebounceClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("soupyong@gmail.com"))
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.email_subject, BuildConfig.VERSION_NAME)
                    )
                }
                it.context.startActivitySafely(intent)
            }
            versionButton.setOnDebounceClickListener {
                it.context.executePlayStoreForApp(BuildConfig.APPLICATION_ID)
            }
            removeAdButton.setOnDebounceClickListener {
                viewModel.onRemoveAdsClick()
            }

            viewModel.showWatermark.observe(viewLifecycleOwner, Observer {
                watermarkSwitch.isChecked = it
            })
            viewModel.latestVersionCode.observe(viewLifecycleOwner, Observer {
                currentVersion.text = if (BuildConfig.VERSION_CODE >= it) {
                    getString(R.string.settings_item_version_latest, BuildConfig.VERSION_NAME)
                } else {
                    getString(R.string.settings_item_version_update, BuildConfig.VERSION_NAME)
                }
            })
            systemViewModel.currentAppearance.observe(viewLifecycleOwner, Observer {
                val currentOptionId = when (it) {
                    Appearance.System -> R.string.settings_item_appearance_system
                    Appearance.Light -> R.string.settings_item_appearance_light
                    Appearance.Dark -> R.string.settings_item_appearance_dark
                }
                currentAppearance.setText(currentOptionId)
            })
            viewModel.removeAdsUiModel.observe(viewLifecycleOwner, Observer {
                removeAdPurchased.isVisible = it.isPurchased
            })
            viewModel.purchaseItemEvent.observe(viewLifecycleOwner, EventObserver {
                purchaseViewModel.purchase(it)
            })
            viewModel.toastEvent.observe(viewLifecycleOwner, EventObserver {
                view.context.toast(it.msg)
            })
            purchaseViewModel.noAdsPurchased.observe(viewLifecycleOwner, Observer {
                adView.isGone = it
                if (it.not()) {
                    adView.loadAd(AdRequest.Builder().build())
                }
            })
            purchaseViewModel.purchaseUpdateEvent.observe(viewLifecycleOwner, EventObserver {
                viewModel.onPurchaseUpdated()
            })

            binding = this
        }
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }
}
