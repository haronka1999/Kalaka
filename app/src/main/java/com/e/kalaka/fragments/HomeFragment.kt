package com.e.kalaka.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.adapters.TagListAdapter
import com.e.kalaka.databinding.FragmentHomeBinding
import com.e.kalaka.utils.Tag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment(), TagListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // initialize recyclerview
        recyclerView = binding.tagRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(binding.root.context, 3)
        recyclerView.addItemDecoration(SpaceGrid(3,Tag.tagCount(), true))
        val adapter = TagListAdapter(Tag.getTags(), this, binding.root.context)
        recyclerView.adapter = adapter

        setOrderButton()

        if(binding.pendingOrdersButton.visibility == View.VISIBLE) {
            binding.pendingOrdersButton.setOnClickListener{
                // TODO: navigate to orders
            }
        }

        return binding.root
    }

    private fun setOrderButton() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance()
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    // class for creating grid in recyclerview
    private inner class SpaceGrid(private val mSpanCount: Int, private val mSpacing: Int, private val mIncludeEdge: Boolean) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % mSpanCount

            if (mIncludeEdge) {
                outRect.left = mSpacing - column * mSpacing / mSpanCount
                outRect.right = (column + 1) * mSpacing / mSpanCount
                if (position < mSpanCount) {
                    outRect.top = mSpacing
                }
                outRect.bottom = mSpacing
            } else {
                outRect.left = column * mSpacing / mSpanCount
                outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount
                if (position < mSpanCount) {
                    outRect.top = mSpacing
                }
            }
        }
    }
}