package com.e.kalaka.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.viewModels.PreloadViewModel

class PendingOrderAdapter(private val list: List<BusinessOrder>) :
    RecyclerView.Adapter<PendingOrderAdapter.DataViewHolder>() {


//    private val preloadedData : PreloadViewModel by activityViewModels()

    // 1. user defined ViewHolder type
    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.prdouctNameTextView)
        val personNameTextView: TextView = itemView.findViewById(R.id.personNameTextView)
        val countProductsTextView: TextView = itemView.findViewById(R.id.countProducts)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val numberTextView: TextView = itemView.findViewById(R.id.numberTextView)


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
        holder.productNameTextView.text = currentItem.productName
        holder.personNameTextView.text = currentItem.clientId
        holder.addressTextView.text =
            "${currentItem.city},  ${currentItem.address}, ${currentItem.postcode} "
        holder.countProductsTextView.text = currentItem.total.toString()
       // holder.numberTextView.text = currentItem


    }


    // 4.
    override fun getItemCount() = list.size
}