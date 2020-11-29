package com.e.kalaka.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentSplashBinding
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.models.Product
import com.e.kalaka.models.User
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class SplashFragment : Fragment() {

    //helpers
    private lateinit var binding: FragmentSplashBinding
    private lateinit var userID: String
    private val preloadedData: PreloadViewModel by activityViewModels()

    //firebase
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private val mUser = mAuth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        preloadedData.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (mUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
            }
        })
        loadUserData()
        return binding.root
    }

    private fun loadUserData() {
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        preloadedData.userEmails.value = mutableListOf()

        userID = mUser?.uid.toString()

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // get all emails from database (is needed for autocomplete search)
                for (user in dataSnapshot.children) {
                    val newValue = Pair(
                        user.child("userId").value.toString(),
                        user.child("email").value.toString()
                    )
                    preloadedData.userEmails.value!!.add(newValue)
                }

                // retrieve user data from database
                val userData = dataSnapshot.child(userID)
                userData.child("favorites").children.forEach {
                    val productId = it.value.toString()
                    addFavoriteProductToViewModel(productId)
                }

                // create new User instance
                val user = User(
                    userData.child("businessId").value.toString(),
                    userData.child("email").value.toString(),
                    mutableListOf(),
                    userData.child("firstName").value.toString(),
                    userData.child("userId").value.toString(),
                    userData.child("lastName").value.toString(),
                    mutableListOf(),
                    userData.child("photoURL").value.toString()
                )
                // set the User instance in the viewmodel
                preloadedData.user.value = user
                loadPendingOrders(user.userId)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(
                    ContentValues.TAG,
                    "Failed to read value.",
                    error.toException()
                )
            }
        })
    }

    private fun loadPendingOrders(userId: String) {

        preloadedData.pendingOrders.value = mutableListOf()
        val myRefBusiness = database.getReference("business")

        myRefBusiness.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // hosszan megyünk a vállalkozásokon
                for(business in snapshot.children) {
                    // hosszan megyünk egy vállalkozás tagjain
                    for(member in business.child("memberIds").children) {
                        // ha a user tagja a vállalkozásnak, akkor a vállalkozás rendelései hozzá tartoznak
                        if(member.value.toString() == userId) {
                            for(orders in business.children ) {
                                if(orders.child("status").value.toString() == "0") {
                                    val order = BusinessOrder(
                                        orders.child("address").value.toString(),
                                        orders.child("city").value.toString(),
                                        orders.child("clientId").value.toString(),
                                        orders.child("comment").value.toString(),
                                        orders.child("number").value.toString(),
                                        orders.child("orderId").value.toString(),
                                        orders.child("postcode").value.toString(),
                                        orders.child("productId").value.toString(),
                                        orders.child("productName").value.toString(),
                                        orders.child("status").value.toString().toInt(),
                                        orders.child("time").value.toString(),
                                        orders.child("total").value.toString().toDouble(),
                                        orders.child("worker").value.toString()
                                    )
                                    preloadedData.pendingOrders.value!!.add(order)
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun addFavoriteProductToViewModel(productId: String) {
        database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productData = snapshot.child(productId)
                val product = Product(
                    productData.child("businessId").value.toString(),
                    productData.child("description").value.toString(),
                    productData.child("name").value.toString(),
                    productData.child("photoUrl").value.toString(),
                    productData.child("price").value.toString().toDouble(),
                    productData.child("productId").value.toString()
                )
                if (preloadedData.favoriteProductlist.value == null) {
                    preloadedData.favoriteProductlist.value = mutableListOf(product)
                } else {
                    preloadedData.favoriteProductlist.value!!.add(product)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}