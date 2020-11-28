package com.e.kalaka.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE

    }
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val view = binding.root

        initializeDatabase()
        showDatas()

        return view
    }



    fun initializeDatabase(){
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun showDatas() {
        var userID = firebaseAuth.currentUser
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //TODO: amikor elkeszul az authentication ki kell cserelni az alabbi path erteket 0-rol userID-ra
                val user = dataSnapshot.child("0")
                binding.firstName.text=user.child("firstName").value.toString()
                binding.lastName.text=user.child("lastName").value.toString()
                binding.email.text=user.child("email").value.toString()
                context?.let {
                    Glide.with(it)
                        .load(user.child("photoURL").value.toString())
                        .into(binding.profilePic)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }

}