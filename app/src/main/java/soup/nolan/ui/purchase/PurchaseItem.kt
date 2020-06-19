package soup.nolan.ui.purchase

sealed class PurchaseItem(
    val skuId: String
) {
    object NoAds : PurchaseItem("no_ads")

    companion object {

        fun all() = listOf<PurchaseItem>(NoAds)

        fun of(skuId: String): PurchaseItem? {
            return all().find { it.skuId == skuId }
        }
    }
}
