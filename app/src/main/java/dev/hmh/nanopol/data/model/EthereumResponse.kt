package dev.hmh.nanopol.data.model

data class EthereumResponse(
    val `data`: EthereumPrices,
    val status: Boolean
)
data class EthereumPrices(
    val price_btc: Double,
    val price_cny: Int,
    val price_eur: Double,
    val price_gbp: Double,
    val price_rur: Int,
    val price_usd: Double
)