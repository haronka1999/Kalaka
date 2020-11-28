package com.e.kalaka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.Business

class BusinessAdapter (
    private val items : List <Business>,
    private val listener : BusinessAdapter.OnItemClickListener
):  RecyclerView.Adapter<BusinessAdapter.DataViewHolder>(){

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val businessName = itemView.findViewById<TextView>(R.id.businessName)
        val businessDescription = itemView.findViewById<TextView>(R.id.business_description)
        val businessImage = itemView.findViewById<ImageView>(R.id.businessLogo)

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessAdapter.DataViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.business_recycle_item, parent, false)
            return DataViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: BusinessAdapter.DataViewHolder, position: Int) {
            val currentItem = items [position]
            holder.businessDescription.text = currentItem.description
            holder.businessName.text = currentItem.name
            //TODO GLIDE
        }

        override fun getItemCount(): Int = items.size

        interface OnItemClickListener{
            fun onItemClick(position: Int)
        }
}
