package com.e.kalaka.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.Business
import com.google.firebase.storage.FirebaseStorage


class BusinessAdapter(
    private var items: MutableList<Business>,
    private val listener: OnItemClickListener
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
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.business_recycle_item,
                parent,
                false
            )
            return DataViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: BusinessAdapter.DataViewHolder, position: Int) {
            val currentItem = items[position]
            holder.businessDescription.text = currentItem.description
            holder.businessName.text = currentItem.name

            setItemImage(currentItem.logoURL, holder)
        }

    private fun setItemImage(logoURL: String, holder: DataViewHolder) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(logoURL)
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            holder.businessImage.setImageBitmap(bmp)
        }
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setData(newItems: MutableList<Business>) {
        items = newItems
        notifyDataSetChanged()
    }
}
