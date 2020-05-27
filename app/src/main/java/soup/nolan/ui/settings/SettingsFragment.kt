package soup.nolan.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import soup.nolan.BuildConfig
import soup.nolan.R
import soup.nolan.databinding.SettingsBinding
import soup.nolan.ui.utils.autoCleared
import soup.nolan.ui.utils.executePlayStoreForApp
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.startActivitySafely

class SettingsFragment : Fragment(R.layout.settings) {

    private var binding: SettingsBinding by autoCleared { adView.destroy() }
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(SettingsBinding.bind(view)) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
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

            viewModel.latestVersionCode.observe(viewLifecycleOwner, Observer {
                currentVersion.text = if (BuildConfig.VERSION_CODE >= it) {
                    getString(R.string.settings_item_version_latest, BuildConfig.VERSION_NAME)
                } else {
                    getString(R.string.settings_item_version_update, BuildConfig.VERSION_NAME)
                }
            })

            adView.loadAd(AdRequest.Builder().build())

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
