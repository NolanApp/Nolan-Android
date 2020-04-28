package soup.nolan.ui

import android.content.Intent
import android.os.Bundle
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import soup.nolan.R
import soup.nolan.ui.base.BaseActivity
import soup.nolan.ui.purchase.PurchaseItem
import soup.nolan.ui.purchase.PurchaseViewModel
import timber.log.Timber

class NolanActivity : BaseActivity(R.layout.nolan_activity) {

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

        billingProcessor.initialize()
        purchaseViewModel.purchaseItemEvent.observe(this, EventObserver {
            billingProcessor.purchase(this, it.skuId)
        })
    }

    override fun onDestroy() {
        billingProcessor.release()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (billingProcessor.handleActivityResult(requestCode, resultCode, data).not()) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
