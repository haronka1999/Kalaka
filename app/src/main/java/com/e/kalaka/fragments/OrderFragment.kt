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
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        val view = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val orderList = ArrayList<UserOrderList>()
        val database = FirebaseDatabase.getInstance()
        val userId = mAuth.currentUser?.uid.toString()
        val ref = database.getReference("users").child(userId).child("orders")
        Log.d("abc","value: $ref")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("abc","value1: $dataSnapshot")
                if(dataSnapshot.hasChildren()) {
                    for (data in dataSnapshot.children) {
                        Log.d("abc","value2: $data")
                        val order = UserOrderList()
                        order.time = data.child("time").value.toString()
                        order.total = data.child("total").value.toString()
                        order.product = data.child("productName").value.toString()
                        val prodId = data.child("productId").value.toString()
                        val productRef = database.getReference("products")
                        Log.d("abc","value3: $prodId")
                        Log.d("abc","value4: $productRef")
                        productRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (product in dataSnapshot.children) {
                                    Log.d("abc","value5: $product")
                                    if (product.key.toString() == prodId) {
                                        val businessId =
                                            product.child("businessId").value.toString()
                                        val businessRef = database.getReference("business")
                                        Log.d("abc","value6: $businessRef")
                                        businessRef.addValueEventListener(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                Log.d("abc","value7: $dataSnapshot")
                                                for (business in dataSnapshot.children) {
                                                    if (business.child("businessId").value.toString() == businessId) {
                                                        order.business =
                                                            business.child("name").value.toString()
                                                        Log.d("abc","value8: $business")
                                                        Log.d("abc","name: ${business.child("name").value.toString()}")
                                                        Log.d("abc","IDDDDDDDDDDDD: $order")
                                                        Log.d("abc","ASASASA: ${order.business}")
                                                        break;
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                // Failed to read value
                                                Log.w(
                                                    "TAG",
                                                    "Failed to read value.",
                                                    error.toException()
                                                )
                                            }
                                        })
                                        break;
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                                Log.w("TAG", "Failed to read value.", error.toException())
                            }
                        })

                        orderList.add(order)
                        Log.d("abc","ORDEEEEER $order")
                    }
                }
                else{
                   Toast.makeText(
                        context, "Meg nincs rendeles",
                        Toast.LENGTH_SHORT
                    ).show()
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