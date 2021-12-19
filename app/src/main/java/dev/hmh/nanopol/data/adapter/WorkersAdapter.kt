package dev.hmh.nanopol.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import dev.hmh.nanopol.action.Listener
import dev.hmh.nanopol.data.model.WorkerItem
import dev.hmh.nanopol.databinding.RowWorkerLayoutBinding
import kotlin.collections.ArrayList

class WorkersAdapter(
    private val context: Context,
    private val arrayList: ArrayList<WorkerItem>,
    private val listener: Listener
) :
    RecyclerView.Adapter<WorkersAdapter.WorkersViewHolder>() {

    val listForSearch = ArrayList<WorkerItem>(arrayList)
    var number = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkersViewHolder {
        return WorkersViewHolder(
            RowWorkerLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WorkersViewHolder, position: Int) {
        holder.bindItem(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class WorkersViewHolder(private val binding: RowWorkerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(worker: WorkerItem) {
            binding.apply {
                txtHashRate.text = worker.hashrate.toString()+"Mh/s"
                txtName.text = worker.id
                txtNumber.text = (++number).toString()
                txtLastShareRating.text = worker.lastShare.toString()+"/"+worker.rating.toString()

            }
            itemView.setOnClickListener {
                listener.getWorkerId(worker.id)
            }
        }

    }

    fun getFilter(): Filter? {
        return workerListFilter
    }

    private val workerListFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val firstList = ArrayList<WorkerItem>()
            if (constraint.isEmpty()) {
                firstList.addAll(listForSearch)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                for (item in listForSearch) {
                    if (item.id.toLowerCase().contains(filterPattern)) {
                        firstList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = firstList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            arrayList.clear()
            arrayList.addAll(results.values as Collection<WorkerItem>)
            notifyDataSetChanged()
        }
    }


}