package com.e.kalaka.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.viewModels.TopicViewModel

class PendingOrderAdapter(
    private var list: List<BusinessOrder>,
    private val listener: OnItemClickListener,
    private val context: Context
) :
    RecyclerView.Adapter<PendingOrderAdapter.DataViewHolder>() {

    // 1. user defined ViewHolder type
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val personNameTextView: TextView = itemView.findViewById(R.id.personNameTextView)
        val countProductsTextView: TextView = itemView.findViewById(R.id.countProducts)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val postalTextView: TextView = itemView.findViewById(R.id.productPostalCode)
        val phoneTextView: TextView = itemView.findViewById(R.id.clientPhone)
        val statusTextView: TextView = itemView.findViewById(R.id.orderStatus)
        val workerTextView: TextView = itemView.findViewById(R.id.orderWorker)
    }


    // 2. Called only a few times = number of items on screen + a few more ones
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_order_recycler_item, parent, false)
        return DataViewHolder(itemView)
    }



    // 3. Called many times, when we scroll the list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = list[position]

        holder.productNameTextView.text = "Termék neve: ${currentItem.productName}"
        holder.countProductsTextView.text = "Darabszám: ${currentItem.total}"
        holder.personNameTextView.text = "Rendelte: ${currentItem.clientId}"
        holder.addressTextView.text = "Kliens címe: ${currentItem.city}, ${currentItem.address}, ${currentItem.postcode}"
        holder.postalTextView.text = "Postakód: ${currentItem.postcode}"
        holder.phoneTextView.text = "Telefonszám: ${currentItem.number}"

        when(currentItem.status.toString()) {
            "0" -> {
                holder.statusTextView.text = "Rendelés állapota: nincs elvállalva"
                holder.workerTextView.visibility = View.GONE
            }
            "1" -> {
                holder.statusTextView.text = "Rendelés állapota: elvállalt"
                holder.workerTextView.text = "Rajta dolgozik: ${currentItem.worker}"
                val iconId = context.resources.getIdentifier("ic_progress", "drawable", context.packageName)
                holder.productNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconId, 0)
            }
            "3" -> {
                holder.statusTextView.text = "Rendelés állapota: postázva"
                holder.workerTextView.visibility = View.GONE
                val iconId = context.resources.getIdentifier("ic_sent", "drawable", context.packageName)
                holder.productNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconId, 0)
            }
            else -> {}
        }
    }

    // 4.
    override fun getItemCount() = list.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }

    fun setData(newList: List<BusinessOrder>) {
        list = newList
        notifyDataSetChanged()
    }
}