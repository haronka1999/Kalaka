package com.e.kalaka.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.adapters.PendingOrderAdapter
import com.e.kalaka.databinding.FragmentPendingOrderBinding
import com.e.kalaka.databinding.PendingOrderRecyclerItemBinding
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.viewModels.PreloadViewModel
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
        Log.d("afaszomkivan", "$businessOrders")
        recyclerView.adapter = PendingOrderAdapter(businessOrders, this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onItemClick(position: Int) {
        // TODO
    }
}