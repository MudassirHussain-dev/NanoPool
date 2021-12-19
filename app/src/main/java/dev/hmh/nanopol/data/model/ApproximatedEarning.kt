package dev.hmh.nanopol.data.model

data class ApproximatedEarning(
    val `data`: ApproximatedEarningItem?,
    val status: Boolean?
)

data class ApproximatedEarningItem(
    val day: Day?,
    val extra_profit_percent: Double?,
    val hour: Hour?,
    val minute: Minute?,
    val month: Month?,
    val prices: Prices?,
    val week: Week?
)
data class Week(
    val bitcoins: Double?,
    val coins: Double?,
    val dollars: Double?,
    val euros: Double?,
    val pounds: Double?,
    val rubles: Double?,
    val yuan: Double?
)
data class Prices(
    val price_btc: Double?,
    val price_cny: Int?,
    val price_eur: Double?,
    val price_gbp: Double?,
    val price_rur: Int?,
    val price_usd: Double?
)
data class Month(
    val bitcoins: Double?,
    val coins: Double?,
    val dollars: Double?,
    val euros: Double?,
    val pounds: Double?,
    val rubles: Double?,
    val yuan: Double?
)
data class Minute(
    val bitcoins: Double?,
    val coins: Double?,
    val dollars: Double?,
    val euros: Double?,
    val pounds: Double?,
    val rubles: Double?,
    val yuan: Double?
)


data class Hour(
    val bitcoins: Double,
    val coins: Double,
    val dollars: Double,
    val euros: Double,
    val pounds: Double,
    val rubles: Double,
    val yuan: Double
)
data class Day(
    val bitcoins: Double,
    val coins: Double,
    val dollars: Double,
    val euros: Double,
    val pounds: Double,
    val rubles: Double,
    val yuan: Double
)