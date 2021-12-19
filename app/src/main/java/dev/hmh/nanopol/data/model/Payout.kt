package dev.hmh.nanopol.data.model

data class Payout(
    val `data`: PayoutItem,
    val status: Boolean
)

data class PayoutItem(
    val payout: Double
)