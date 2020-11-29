package com.e.kalaka.adapters

import android.annotation.SuppressLint
import android.graphics.Color
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
    private val list: List<BusinessOrder>,
    private val listener: OnItemClickListener) :
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

        val productNameTextView: TextView = itemView.findViewById(R.id.prdouctNameTextView)
        val personNameTextView: TextView = itemView.findViewById(R.id.personNameTextView)
        val countProductsTextView: TextView = itemView.findViewById(R.id.countProducts)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val numberTextView: TextView = itemView.findViewById(R.id.numberTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
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
        holder.personNameTextView.text = "Kliens neve: ${currentItem.clientId}"
        holder.addressTextView.text =
            "Kliens címe: ${currentItem.city}, ${currentItem.address}, ${currentItem.postcode} "
        holder.countProductsTextView.text = "Darabszám:  ${currentItem.total}"
        holder.numberTextView.text = "Összeg: 100 RON"


    }

    // 4.
    override fun getItemCount() = list.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }
}