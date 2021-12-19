package dev.hmh.nanopol.data.model

data class GeneralInfo(
    val `data`: GeneralInfoDetail,
    val status: Boolean
)
data class GeneralInfoDetail(
    val account: String,
    val avgHashrate: AvgHashRateItem,
    val balance: String,
    val hashrate: String,
    val unconfirmed_balance: String,
    val workers: List<Workers>
)
data class Workers(
    val h1: String,
    val h12: String,
    val h24: String,
    val h3: String,
    val h6: String,
    val hashrate: String,
    val id: String,
    val lastshare: Int,
    val rating: Int,
    val uid: Int
)