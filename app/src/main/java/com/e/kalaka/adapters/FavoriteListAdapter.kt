package com.e.kalaka.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        holder.itemView.findViewById<TextView>(R.id.product_name).text = currentItem.name
        holder.itemView.findViewById<TextView>(R.id.product_description).text = currentItem.description
        holder.itemView.findViewById<TextView>(R.id.product_price).text = "${currentItem.price} RON"
        holder.itemView.findViewById<ImageView>(R.id.favorite_product).visibility = View.INVISIBLE
        holder.itemView.findViewById<ImageView>(R.id.delete_product).setOnClickListener{
            favorites.remove(currentItem)
            removeFavoriteItemFromDatabase(currentItem)
        }
    }

    private fun removeFavoriteItemFromDatabase(currentItem: Product) {
        val mUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val databaseRef = database.getReference("users")

        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = mUser?.let { snapshot.child(it.uid).child("favorites") }
                Log.d("DELETE", "$userData")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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