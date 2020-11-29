package com.e.kalaka.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.kalaka.R
import com.e.kalaka.adapters.PendingOrderAdapter
import com.e.kalaka.databinding.FragmentPendingOrderBinding
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.models.Product
import com.e.kalaka.viewModels.PreloadViewModel
import com.e.kalaka.viewModels.TopicViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PendingOrderFragment : Fragment(), PendingOrderAdapter.OnItemClickListener {

    private lateinit var binding: FragmentPendingOrderBinding
    private var businessOrders = mutableListOf<BusinessOrder>()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    var userId = mAuth.currentUser?.uid
    var myRefBusiness = database.getReference("business")
    private val preloadedData: PreloadViewModel by activityViewModels()
    private val topicViewModel: TopicViewModel by activityViewModels()
    private lateinit var businessId: String
    private var task = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        businessOrders = preloadedData.pendingOrders.value!!
        businessId = preloadedData.user.value!!.businessId


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pending_order, container, false)
        val recyclerView = binding.recyclerView
        recyclerView.adapter = PendingOrderAdapter(businessOrders, this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

//        if(businessId == "0"){
//            //WE SHOUDLNT BE HERE BECAUSE THE BUTTON IS NOT
//            //VISIBLE IF THE USER DOESNT HAVE A BUSINESS
//        }

        topicViewModel.choosedTask.observe(viewLifecycleOwner, Observer {
            task = topicViewModel.choosedTask.value!!.toInt()
            Log.d("Helo", task.toString())

            myRefBusiness.child(businessId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Helo", "$snapshot")
                    if(task == 1){
                        Log.d("Helo", "Current Order ID : " + preloadedData.currentPendingOrder.orderId)

                    }else if(task == 2){

                    }else if(task == 3){

                    }else if(task == 4){

                    }else{

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        })



//        topicViewModel.choosedTask.observe(viewLifecycleOwner, Observer {
//            myRefBusiness.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (business in snapshot.children) {
//                        if (business.child("ownerId").value.toString() == userId) {
//                            if (business.child("orders").value != null) {
//                                for (orders in business.child("orders").children) {
//                                    if (orders.child("status").value.toString() == "0") {
//                                        Log.d(
//                                            "Helo",
//                                            "orders: ${orders.child("orderId").value.toString()}"
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })
//        })
        return binding.root
    }

    override fun onItemClick(position: Int) {
        Log.d("Helo", "Positiion: $position")
        preloadedData.currentPendingOrder = preloadedData.pendingOrders.value!![position]
        val fm = AddTaskPopUpFragment()
        parentFragmentManager.let { it1 ->
            fm.show(it1, "")
        }
    }


}

