package soup.nolan.ui.purchase

sealed class PurchaseItem(
    val skuId: String
) {
    object NoAds : PurchaseItem("no_ads")
    object BuyCoffee01 : PurchaseItem("buy_coffee_01")
    object BuyCoffee02 : PurchaseItem("buy_coffee_02")
    object BuyCoffee03 : PurchaseItem("buy_coffee_03")
    object BuyCoffee04 : PurchaseItem("buy_coffee_04")
    object BuyCoffee05 : PurchaseItem("buy_coffee_05")

    companion object {

        fun all(): List<PurchaseItem> {
            return listOf(
                NoAds,
                BuyCoffee01,
                BuyCoffee02,
                BuyCoffee03,
                BuyCoffee04,
                BuyCoffee05
            )
        }

        fun of(skuId: String): PurchaseItem? {
            return all().find { it.skuId == skuId }
        }
    }
}
