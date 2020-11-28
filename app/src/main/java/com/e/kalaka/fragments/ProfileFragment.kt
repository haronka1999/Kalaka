package com.e.kalaka.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
        Log.d("value", "itt")
        Log.d("value", "database $databaseRef")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("value", "ott")
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.child("0")
                Log.d("value", "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        Log.d("value", "amott")

    }

}