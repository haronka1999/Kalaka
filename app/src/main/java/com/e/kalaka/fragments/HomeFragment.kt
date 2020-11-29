package com.e.kalaka.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.adapters.TagListAdapter
import com.e.kalaka.databinding.FragmentHomeBinding
import com.e.kalaka.models.Business
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.utils.Tag
import com.e.kalaka.viewModels.TopicViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.concurrent.timerTask

class HomeFragment : Fragment(), TagListAdapter.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private val topicViewModel : TopicViewModel by activityViewModels()
    private lateinit var database : FirebaseDatabase

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

        val selectedTopic = Tag.getTags()[position].second
        startLoadingData(selectedTopic)
        topicViewModel.list.value = listOf()
        findNavController().navigate(R.id.action_homeFragment_to_mainSearch)
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

    private fun startLoadingData(selectedTopic : String){
        database = FirebaseDatabase.getInstance()

        val reference = database.getReference("business")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<Business>()

                for (business in snapshot.children ){

                    for (tag in business.child("tags").children){

                        if (tag.value.toString() == selectedTopic){
                            val tags = mutableListOf<String>()
                            val memberIds = mutableListOf<String>()
                            val productIds = mutableListOf<String>()
                            val orders = mutableListOf<BusinessOrder>()

                            for (tag in business.child("tags").children){
                                tags.add(tag.value.toString())
                            }
                            for (memberId in business.child("memberIds").children){
                                memberIds.add(memberId.value.toString())
                            }
                            for (productId in business.child("productIds").children){
                                productIds.add(productId.value.toString())
                            }
                            for (order in business.child("orders").children){
                                val ord = BusinessOrder(
                                    order.child("address").value.toString(),
                                    order.child("city").value.toString(),
                                    order.child("clientId").value.toString(),
                                    order.child("comment").value.toString(),
                                    order.child("number").value.toString().toInt(),
                                    order.child("orderId").value.toString(),
                                    order.child("postcode").value.toString(),
                                    order.child("productId").value.toString(),
                                    order.child("productName").value.toString(),
                                    order.child("status").value.toString(),
                                    order.child("time").value.toString(),
                                    order.child("total").value.toString().toDouble(),
                                    order.child("worker").value.toString()
                                )
                                orders.add(ord)
                            }

                            val item = Business(
                                business.child("businessId").value.toString(),
                                business.child("description").value.toString(),
                                business.child("email").value.toString(),
                                business.child("facebookURL").value.toString(),
                                business.child("instagramURL").value.toString(),
                                business.child("location").value.toString(),
                                business.child("logoURL").value.toString(),
                                memberIds,
                                business.child("name").value.toString(),
                                orders,
                                business.child("ownerId").value.toString(),
                                business.child("phone").value.toString(),
                                productIds,
                                tags
                            )

                            list.add(item)
                        }
                    }
                }
                //Log.d("******",list.toString())
                topicViewModel.list.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var callbackCounter = 0
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (callbackCounter == 0) {
                Toast.makeText(requireContext(), "Press again to exit", Toast.LENGTH_SHORT).show()
                Timer().schedule(timerTask {
                    callbackCounter = 0
                }, 2000)

                callbackCounter++
            } else requireActivity().finish()
        }
    }


   /* private fun toBusiness(obj : DataSnapshot) : Business{
        return Business(
            obj.child("")
        )
    }*/
}