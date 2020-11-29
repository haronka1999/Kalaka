package com.e.kalaka.adapters

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.renderscript.Sampler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.google.firebase.storage.FirebaseStorage

class ProductAdapter (
    private val items : List <Product>,
    private val listener : ProductAdapter.OnItemClickListener,
    private val activity : Activity,
    private val indicator : Int
        ):  RecyclerView.Adapter<ProductAdapter.DataViewHolder>() {

    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        val deleteProduct = itemView.findViewById<ImageButton>(R.id.delete_product)
        val favoriteProduct = itemView.findViewById<ImageButton>(R.id.favorite_product)

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
        val userId = auth.currentUser?.uid.toString()
        setIconColor(position,userId,holder.favoriteProduct)



        setProductImage(currentItem.photoURL, holder.productImage)

        if (indicator == 2){
            holder.deleteProduct.visibility = View.GONE
        }
        else {
            holder.deleteProduct.setOnClickListener {

            }
        }
        holder.favoriteProduct.setOnClickListener{
            likeProduct(position,holder.favoriteProduct)
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

    private fun likeProduct(position : Int, icon : ImageButton){
        val userId = auth.currentUser?.uid.toString()
        database.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                var isLiked = false
                var favId : String? = null
                for (favorite in snapshot.child(userId).child("favorites").children){
                    if (favorite.value.toString() == items[position].productId)
                    {
                        isLiked = true
                        favId = favorite.key
                    }
                }

                if (isLiked){
                    database.child(userId).child("favorites").child(favId!!).removeValue()
                    icon.setColorFilter(Color.argb(255, 68, 190, 237))
                }
                else{
                    database.child(userId).child("favorites").child(items[position].productId).setValue(items[position].productId)
                    icon.setColorFilter(Color.argb(255, 194, 39, 72))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setIconColor(position : Int, userId : String, icon : ImageButton){

        database.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                var isLiked = false
                for (favorite in snapshot.child(userId).child("favorites").children){
                    if (favorite.value.toString() == items[position].productId)
                    {
                        isLiked = true
                        break
                    }
                }
                if (isLiked){
                    icon.setColorFilter(Color.argb(255, 194, 39, 72))
                }
                else{
                    icon.setColorFilter(Color.argb(255, 68, 190, 237))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}