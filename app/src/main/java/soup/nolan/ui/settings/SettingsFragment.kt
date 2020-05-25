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
import com.google.android.gms.ads.AdRequest
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.databinding.SettingsBinding
import soup.nolan.ui.EventObserver
import soup.nolan.ui.purchase.PurchaseViewModel
import soup.nolan.ui.utils.*

class SettingsFragment : Fragment(R.layout.settings) {

    private var binding: SettingsBinding by autoCleared { adView.destroy() }
    private val viewModel: SettingsViewModel by viewModels()
    private val purchaseViewModel: PurchaseViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SettingsBinding.bind(view)) {
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
            buyCoffeeButton.setOnDebounceClickListener {
                viewModel.onBuyCoffeeClick()
            }

            viewModel.latestVersionCode.observe(viewLifecycleOwner, Observer {
                currentVersion.text = if (BuildConfig.VERSION_CODE >= it) {
                    getString(R.string.settings_item_version_latest, BuildConfig.VERSION_NAME)
                } else {
                    getString(R.string.settings_item_version_update, BuildConfig.VERSION_NAME)
                }
            })
            viewModel.removeAdsUiModel.observe(viewLifecycleOwner, Observer {
                removeAdPurchased.isVisible = it.isPurchased
            })
            viewModel.buyCoffeeUiModel.observe(viewLifecycleOwner, Observer {
                buyCoffeeGroup.isVisible = it.isVisible
                coffeePower01.isChecked = it.isPurchased01
                coffeePower02.isChecked = it.isPurchased02
                coffeePower03.isChecked = it.isPurchased03
                coffeePower04.isChecked = it.isPurchased04
                coffeePower05.isChecked = it.isPurchased05
            })
            viewModel.purchaseItemEvent.observe(viewLifecycleOwner, EventObserver {
                purchaseViewModel.purchase(it)
            })
            viewModel.toastEvent.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    ToastUiModel.AlreadyNoAdsPurchased ->
                        view.context.toast(R.string.toast_already_no_ads_purchased)
                    ToastUiModel.AlreadyBuyAllCoffees ->
                        view.context.toast(R.string.toast_already_buy_all_coffees)
                }
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
//        binding.adView.resume()
    }

    override fun onPause() {
//        binding.adView.pause()
        super.onPause()
    }
}
