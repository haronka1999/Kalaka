package com.e.kalaka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.UserOrder
import com.e.kalaka.models.UserOrderList

class UserOrderAdapter(private val userOrderList: List<UserOrderList>) : RecyclerView.Adapter<UserOrderAdapter.UserOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_order_recycle_item,parent,false)
        return UserOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserOrderViewHolder, position: Int) {
        val currentItem = userOrderList[position]
        holder.time.text=currentItem.time
        holder.business.text=currentItem.business
        holder.total.text= currentItem.total
        holder.product.text=currentItem.product

    }

    override fun getItemCount()= userOrderList.size

    class UserOrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val time: TextView = itemView.findViewById<TextView>(R.id.timeText)
        val business: TextView = itemView.findViewById<TextView>(R.id.businessText)
        val total: TextView = itemView.findViewById<TextView>(R.id.totalText)
        val product: TextView = itemView.findViewById<TextView>(R.id.productText)

    }
}