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
import androidx.lifecycle.observe
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
        businessId = preloadedData.user.value!!.businessId
        topicViewModel.choosedTask.value = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pending_order, container, false)
        val recyclerView = binding.recyclerView
        val adapter = PendingOrderAdapter(preloadedData.pendingOrders.value!!, this, binding.root.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        preloadedData.pendingOrders.observe(viewLifecycleOwner) {
            if(it.size == 0) {
                binding.empty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.empty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }

            adapter.setData(it)
        }

        topicViewModel.choosedTask.observe(viewLifecycleOwner, Observer {

            task = topicViewModel.choosedTask.value!!.toInt()
                when (task) {
                    1 -> {
                        val orderId = preloadedData.currentPendingOrder.orderId
                        val workerName = "${preloadedData.user.value?.lastName} ${preloadedData.user.value?.firstName}"
                        setOrderStatus(businessId, orderId, workerName, "1")
                    }
                    2 -> {
                        val orderId = preloadedData.currentPendingOrder.orderId
                        val workerName = "${preloadedData.user.value?.lastName} ${preloadedData.user.value?.firstName}"
                        setOrderStatus(businessId, orderId, workerName, "2")
                    }
                    3 -> {
                        val orderId = preloadedData.currentPendingOrder.orderId
                        val workerName = "${preloadedData.user.value?.lastName} ${preloadedData.user.value?.firstName}"
                        setOrderStatus(businessId, orderId, workerName, "3")
                    }
                    4 -> {
                    }
                    else -> {

                    }
                }
        })

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

    private fun setOrderStatus(businessId: String, orderId: String, worker: String, status: String) {
        myRefBusiness.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child(businessId).child("orders").child(orderId).child("status").ref.setValue(status)
                snapshot.child(businessId).child("orders").child(orderId).child("worker").ref.setValue(worker)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

