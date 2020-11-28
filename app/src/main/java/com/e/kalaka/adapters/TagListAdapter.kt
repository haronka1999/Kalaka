package com.e.kalaka.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R

class TagListAdapter(private var tags: List<Pair<String, String>>, private val listener: OnItemClickListener, private val context: Context): RecyclerView.Adapter<TagListAdapter.TagListHolder>() {

    inner class TagListHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagListHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tag_item_layout, parent, false)
        return TagListHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagListHolder, position: Int) {
        val currentItem = tags[position]
        val textView = holder.itemView.findViewById<TextView>(R.id.tagText)
        val iconId = context.resources.getIdentifier(currentItem.first, "drawable", context.packageName)
        textView.text = currentItem.second
        textView.setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0)


        val color = ContextCompat.getColor(context, R.color.button_yellow)
        textView.compoundDrawables[1].setTint(color)
    }

    override fun getItemCount(): Int = tags.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}