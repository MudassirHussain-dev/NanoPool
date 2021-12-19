package dev.hmh.nanopol.data.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.hmh.nanopol.data.model.PaymentItem
import dev.hmh.nanopol.databinding.RowPaymentLayoutBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentAdapter(private val arrayList: ArrayList<PaymentItem>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder(
            RowPaymentLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bindItem(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class PaymentViewHolder(private val binding: RowPaymentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bindItem(item: PaymentItem) {
            binding.apply {
                txtAmount.text = item.amount.toString()+"eth"
                txtConfirmed.text = "Confirmed"
                txtDateTime.text = (SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format( Date((item.date) * 1000))).toString()
                txtHash.text = item.txHash
            }
        }
    }

}