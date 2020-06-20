package soup.nolan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.window.DeviceState
import androidx.window.WindowManager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.R
import soup.nolan.ui.camera.CameraFragmentDirections.Companion.actionToEdit
import soup.nolan.ui.purchase.PurchaseItem
import soup.nolan.ui.purchase.PurchaseViewModel
import soup.nolan.ui.system.SystemViewModel
import soup.nolan.ui.utils.findNavHostFragment
import timber.log.Timber
import java.util.concurrent.Executor

class NolanActivity : AppCompatActivity(R.layout.nolan_activity) {

    private var windowManager: WindowManager? = null
    private val handler = Handler(Looper.getMainLooper())
    private val mainThreadExecutor = Executor { r: Runnable -> handler.post(r) }
    private val deviceStateChangeCallback = Consumer<DeviceState> { newDeviceState ->
        systemViewModel.onDeviceStateChanged(newDeviceState)
    }

    private val systemViewModel: SystemViewModel by viewModels()

    private val purchaseViewModel: PurchaseViewModel by viewModels()

    private val billingProcessor: BillingProcessor by lazy {
        BillingProcessor(
            this,
            getString(R.string.google_play_license_key),
            object : BillingProcessor.IBillingHandler {

                override fun onBillingInitialized() {
                    updatePurchases()
                }

                override fun onPurchaseHistoryRestored() {
                    updatePurchases()
                }

                override fun onProductPurchased(productId: String, details: TransactionDetails?) {
                    PurchaseItem.of(productId)?.let {
                        purchaseViewModel.onPurchased(it)
                        purchaseViewModel.onPurchaseUpdate()
                    }
                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {
                    Timber.e(error, "onBillingError: errorCode=$errorCode")
                }

                private fun updatePurchases() {
                    PurchaseItem.all().forEach {
                        if (billingProcessor.isPurchased(it.skuId)) {
                            purchaseViewModel.onPurchased(it)
                        } else {
                            purchaseViewModel.onNotPurchased(it)
                        }
                    }
                    purchaseViewModel.onPurchaseUpdate()
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Nolan_Main)
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            billingProcessor.initialize()
        }
        purchaseViewModel.purchaseItemEvent.observe(this, EventObserver {
            billingProcessor.purchase(this, it.skuId)
        })

        windowManager = WindowManager(this, null).apply {
            registerDeviceStateChangeCallback(
                mainThreadExecutor,
                deviceStateChangeCallback
            )
            systemViewModel.onDeviceStateChanged(deviceState)
        }
        intent?.handleDeepLink()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.handleDeepLink()
    }

    override fun onDestroy() {
        billingProcessor.release()
        windowManager?.unregisterDeviceStateChangeCallback(deviceStateChangeCallback)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data).not()) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun Intent.handleDeepLink() {
        if (action == Intent.ACTION_SEND) {
            if (type?.startsWith("image/") == true) {
                handleSendImage(this)
            }
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            val navHostFragment = findNavHostFragment(R.id.nav_host_fragment)
            navHostFragment.navController.navigate(actionToEdit(it, true, false))
        }
    }
}
