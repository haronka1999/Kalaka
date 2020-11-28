package com.e.kalaka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.Product

class FavoriteListAdapter(private val favorites: List<Product>, private val listener: OnItemClickListener): RecyclerView.Adapter<FavoriteListAdapter.FavoriteListHolder>() {

    inner class FavoriteListHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_recylce_item, parent, false)
        return FavoriteListHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteListHolder, position: Int) {
        val currentItem = favorites[position]
        holder.itemView.findViewById<TextView>(R.id.product_name).text = currentItem.name
        holder.itemView.findViewById<TextView>(R.id.product_description).text = currentItem.description
        holder.itemView.findViewById<TextView>(R.id.product_price).text = "${currentItem.price} RON"
        holder.itemView.findViewById<Button>(R.id.favorite_product).visibility = View.INVISIBLE
        holder.itemView.findViewById<Button>(R.id.favorite_product).setOnClickListener{
            // TODO
        }
    }

    override fun getItemCount(): Int = favorites.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}