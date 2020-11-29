package com.e.kalaka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.BusinessOrder

class PendingOrderAdapter(private val list: List<BusinessOrder>) : RecyclerView.Adapter<PendingOrderAdapter.DataViewHolder>()  {

    // 1. user defined ViewHolder type
    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    // 2. Called only a few times = number of items on screen + a few more ones
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pending_order_recycler_item, parent, false)
        return DataViewHolder(itemView)
    }



    // 3. Called many times, when we scroll the list
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {}


    // 4.
    override fun getItemCount() = list.size
}