package com.e.kalaka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R

class BusinessProfileAdapter (
    private val items : List <Int>,
    private val listener : BusinessProfileAdapter.OnItemClickListener
        ):  RecyclerView.Adapter<BusinessProfileAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_recylce_item, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = items [position]
        //holder.cityName.text = currentItem
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}