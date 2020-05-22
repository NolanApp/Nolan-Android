package soup.nolan.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.window.DeviceState
import androidx.window.WindowManager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.nolan.R
import soup.nolan.ui.base.BaseActivity
import soup.nolan.ui.purchase.PurchaseItem
import soup.nolan.ui.purchase.PurchaseViewModel
import soup.nolan.ui.system.SystemViewModel
import timber.log.Timber
import java.util.concurrent.Executor

class NolanActivity : BaseActivity(R.layout.nolan_activity) {

    private val handler = Handler(Looper.getMainLooper())
    private val mainThreadExecutor = Executor { r: Runnable -> handler.post(r) }
    private lateinit var windowManager: WindowManager
    private val deviceStateChangeCallback = Consumer<DeviceState> { newDeviceState ->
        systemViewModel.onDeviceStateChanged(newDeviceState)
    }

    private val systemViewModel: SystemViewModel by viewModel()

    private val purchaseViewModel: PurchaseViewModel by viewModel()

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

        windowManager = WindowManager(this, null)
        windowManager.registerDeviceStateChangeCallback(
            mainThreadExecutor,
            deviceStateChangeCallback
        )
        systemViewModel.onDeviceStateChanged(windowManager.deviceState)
    }

    override fun onDestroy() {
        billingProcessor.release()
        windowManager.unregisterDeviceStateChangeCallback(deviceStateChangeCallback)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data).not()) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
