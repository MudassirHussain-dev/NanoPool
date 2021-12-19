package dev.hmh.nanopol.data.model

data class Payment(
    val `data`: List<PaymentItem>,
    val status: Boolean
)
data class PaymentItem(
    val amount: Double,
    val confirmed: Boolean,
    val date: Long,
    val txHash: String
)