package dev.hmh.nanopol.data.model

data class AvgHashRate(
    val status: Boolean?,
    val data: AvgHashRateItem?,
)
data class AvgHashRateItem(
    val h1: String,
    val h12: String,
    val h24: String,
    val h3: String,
    val h6: String
)
