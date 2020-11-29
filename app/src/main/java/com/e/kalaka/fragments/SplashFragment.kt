package com.e.kalaka.fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentRegisterBinding
import com.e.kalaka.databinding.FragmentSplashBinding
import com.e.kalaka.models.Product
import com.e.kalaka.models.User
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap


class SplashFragment : Fragment() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSplashBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userID: String
    private val preloadedData: PreloadViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        // get current user
        mAuth = FirebaseAuth.getInstance();
        val mUser = mAuth.currentUser

        Timer().schedule(object : TimerTask() {
            override fun run() {
                // if user is logged in, initialize attributes for viewmodel
                if (mUser != null) {
                    // get reference for users document
                    database = FirebaseDatabase.getInstance()
                    databaseRef = database.getReference("users")

                    userID = mUser.uid.toString()
                    val emails = mutableListOf<Pair<String, String>>()
                    val favoriteProductIds = mutableListOf<String>()

                    databaseRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // get all emails from database (is needed for autocomplete search)
                            for (user in dataSnapshot.children) {
                                val newValue = Pair(user.child("userId").value.toString(),user.child("email").value.toString())
                                emails.add(newValue)
                            }
                            preloadedData.userEmails.value = emails

                            // retrieve user data from database
                            val userData = dataSnapshot.child(userID)
                             userData.child("favorites").children.forEach{
                                 val productId = it.value.toString()
                                 addFavoriteProductToViewModel(productId)
                            }

                            // create new User instance
                            val user = User(
                                0,
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
                    Log.d("RETURN", "mainScreen");
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                } else {
                    Log.d("RETURN", "showLoginScreen");
                    findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                }

            }
        }, 2000)

        return binding.root
    }

    private fun addFavoriteProductToViewModel(productId: String) {
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("products")

        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productData = snapshot.child(productId)
                val product = Product(productData.child("businessId").value.toString(),
                                    productData.child("description").value.toString(),
                                    productData.child("name").value.toString(),
                                    productData.child("photoUrl").value.toString(),
                                    productData.child("price").value.toString().toDouble(),
                                    productData.child("productId").value.toString()
                )
                if(preloadedData.favoriteProductlist.value == null) {
                    preloadedData.favoriteProductlist.value = mutableListOf(product)
                }
                else {
                    preloadedData.favoriteProductlist.value!!.add(product)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}