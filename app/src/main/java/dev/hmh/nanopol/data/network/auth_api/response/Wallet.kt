package dev.hmh.nanopol.data.network.auth_api.response

data class Wallet(
    val error: String,
    val wallet: List<WalletDetail>
)

data class WalletDetail(
    val Id: String,
    val date: String,
    val username: String,
    val walletId: String,
    val wallet_name: String
)