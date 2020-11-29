package com.e.kalaka.adapters

import android.app.Activity
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.models.Product
import com.google.firebase.storage.FirebaseStorage

class ProductAdapter (
    private val items : List <Product>,
    private val listener : ProductAdapter.OnItemClickListener,
    private val activity : Activity,
    private val indicator : Int
        ):  RecyclerView.Adapter<ProductAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        val productDescription = itemView.findViewById<TextView>(R.id.product_description)
        val productName = itemView.findViewById<TextView>(R.id.product_name)
        val productPrice = itemView.findViewById<TextView>(R.id.product_price)
        val deleteProduct = itemView.findViewById<ImageView>(R.id.delete_product)
        val favoriteProduct = itemView.findViewById<ImageView>(R.id.favorite_product)

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
        //Glide.with(activity).load(Uri.parse(currentItem.photoURL)).into(holder.productImage)
        setProductImage(currentItem.photoURL, holder.productImage)

        if (indicator == 2){
            holder.deleteProduct.visibility = View.GONE
        }
        else {
            holder.deleteProduct.setOnClickListener {

            }
        }
        holder.favoriteProduct.setOnClickListener{
            // Todo
        }
    }

    private fun setProductImage(photoURL: String, productImage: ImageView) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(photoURL)
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            productImage.setImageBitmap(bmp)
        }
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}