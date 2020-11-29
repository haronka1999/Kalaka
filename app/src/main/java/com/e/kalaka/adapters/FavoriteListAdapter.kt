package com.e.kalaka.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FavoriteListAdapter(private var favorites: MutableList<Product>, private val listener: OnItemClickListener): RecyclerView.Adapter<FavoriteListAdapter.FavoriteListHolder>() {

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
        holder.itemView.findViewById<ImageView>(R.id.favorite_product).visibility = View.INVISIBLE
        holder.itemView.findViewById<ImageView>(R.id.delete_product).setOnClickListener{
            favorites.remove(currentItem)
            removeFavoriteItemFromDatabase(currentItem)
        }
        setItemImage(currentItem.photoURL, holder.itemView.findViewById(R.id.product_image))
    }

    private fun removeFavoriteItemFromDatabase(currentItem: Product) {
        val mUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val databaseRef = database.getReference("users").child(mUser!!.uid).child("favorites").child(currentItem.productId)
        databaseRef.removeValue()
    }

    private fun setItemImage(logoURL: String, imageView: ImageView) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(logoURL)
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            imageView.setImageBitmap(bmp)
        }
    }


    override fun getItemCount(): Int = favorites.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setData(products: MutableList<Product>) {
        favorites = products
        notifyDataSetChanged()
    }
}