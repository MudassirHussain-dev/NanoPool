package dev.hmh.nanopol.ui.auth.args

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArgumentWallet(
    val Id: String,
    val strWallet: String,
    val strType: String,

    ) : Parcelable
