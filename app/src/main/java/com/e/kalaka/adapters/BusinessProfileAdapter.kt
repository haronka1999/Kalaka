package com.e.kalaka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.models.Product

class BusinessProfileAdapter (
    private val items : List <Product>,
    private val listener : BusinessProfileAdapter.OnItemClickListener
        ):  RecyclerView.Adapter<BusinessProfileAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        val productDescription = itemView.findViewById<TextView>(R.id.product_description)
        val productName = itemView.findViewById<TextView>(R.id.product_name)
        val productPrice = itemView.findViewById<TextView>(R.id.product_price)

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
        holder.productDescription.text = currentItem.description
        holder.productName.text = currentItem.name
        holder.productPrice.text = currentItem.price.toString() + " RON"
        //Glide.with()

    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}