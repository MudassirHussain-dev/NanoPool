package dev.hmh.nanopol.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.hmh.nanopol.action.Listener
import dev.hmh.nanopol.action.UpdateListener
import dev.hmh.nanopol.data.network.auth_api.response.WalletDetail
import dev.hmh.nanopol.databinding.RowWalletListBinding

class WalletListAdapter(
    val context: Context,
    val arrayList: ArrayList<WalletDetail>,
    val listener: Listener,
    val updateListener: UpdateListener
) :
    RecyclerView.Adapter<WalletListAdapter.WalletListViewModel>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletListViewModel {
        return WalletListViewModel(
            RowWalletListBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WalletListViewModel, position: Int) {
        holder.bindItem(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class WalletListViewModel(val binding: RowWalletListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: WalletDetail) {
            binding.apply {
                txtWallet.text = item.walletId
            }
            binding.ivDelete.setOnClickListener {
                updateListener.getAccountId(item.Id, item.walletId,"d")
            }
            binding.ivUpdate.setOnClickListener {
                updateListener.getAccountId(item.Id, item.walletId,"u")
            }
            binding.txtWallet.setOnClickListener {
                listener.getWorkerId(item.walletId)
            }
        }

    }
}