package dev.hmh.nanopol.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.hmh.nanopol.data.model.ApproximatedEarningItem
import dev.hmh.nanopol.databinding.RowCaculateLayoutBinding

class CalculateAdapter(
    private val context: Context,
    private val arrayList: ArrayList<ApproximatedEarningItem>
) : RecyclerView.Adapter<CalculateAdapter.CalculateViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):CalculateViewHolder {
        return CalculateViewHolder(
            RowCaculateLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CalculateViewHolder, position: Int) {
        holder.bindItem(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class CalculateViewHolder(private val binding: RowCaculateLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: ApproximatedEarningItem) {
            binding.apply {

            }
        }

    }

}
