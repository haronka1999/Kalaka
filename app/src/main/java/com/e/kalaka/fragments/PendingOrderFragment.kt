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
import com.e.kalaka.viewModels.PreloadViewModel
import com.e.kalaka.viewModels.TopicViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class PendingOrderFragment : Fragment(), PendingOrderAdapter.OnItemClickListener {

    private lateinit var binding: FragmentPendingOrderBinding
    private var businessOrders = mutableListOf<BusinessOrder>()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    var userId = mAuth.currentUser?.uid
    var myRefBusiness = database.getReference("business")
    private val preloadedData: PreloadViewModel by activityViewModels()
    private val topicViewModel: TopicViewModel by activityViewModels()
    private var task = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        businessOrders = preloadedData.pendingOrders.value!!

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

        topicViewModel.choosedTask.observe(viewLifecycleOwner, Observer {
            task = topicViewModel.choosedTask.value?.toString()!!.toInt()
            Log.d("Helo", "Task: $task")
        })


        return binding.root
    }

    override fun onItemClick(position: Int) {
        Log.d("Helo", "Positiion: ${position.toString()}")
        val fm = AddTaskPopUpFragment()
        parentFragmentManager.let { it1 ->
            fm.show(it1, "")
        }
    }
}

