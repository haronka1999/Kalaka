package com.e.kalaka.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.kalaka.R
import com.e.kalaka.adapters.UserOrderAdapter
import com.e.kalaka.databinding.FragmentOrderBinding
import com.e.kalaka.models.UserOrderList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        val view = binding.root

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val orderList = ArrayList<UserOrderList>()

        val database = FirebaseDatabase.getInstance()
        val userId = mAuth.currentUser?.uid.toString()
        val ref = database.getReference("users").child(userId).child("orders")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    binding.noOrderText.visibility = View.GONE
                    for (data in dataSnapshot.children) {
                        val order = UserOrderList()
                        order.time = data.child("time").value.toString()
                        order.total = data.child("total").value.toString()
                        order.product = data.child("productName").value.toString()
                        order.business = data.child("businessName").value.toString()

                        orderList.add(order)
                    }
                }
                else{
                   binding.recyclerView.visibility = View.GONE
                }

                if (orderList.size == 0){
                    binding.noOrderText.visibility = View.GONE
                }
                binding.recyclerView.adapter = UserOrderAdapter(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_orderFragment_to_homeFragment)
        }
    }
}