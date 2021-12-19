package dev.hmh.nanopol.data.model

data class Chart(
    val status: Boolean?,
    val data: List<ChartItem>,
)

data class ChartItem(
    val date: Long,
    val hashrate: Int,
    val shares: Int
)

