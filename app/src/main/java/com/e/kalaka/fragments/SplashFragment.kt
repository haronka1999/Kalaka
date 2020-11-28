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
import com.e.kalaka.models.User
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*


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
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        mAuth = FirebaseAuth.getInstance();
        val mUser = mAuth.currentUser

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (mUser != null) {
                    database = FirebaseDatabase.getInstance()
                    databaseRef = database.getReference("users")
                    firebaseAuth = FirebaseAuth.getInstance()
                    userID = firebaseAuth.currentUser?.uid.toString()
                    databaseRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.child(userID)
                            val u = User(
                                0,
                                user.child("email").value.toString(),
                                mutableListOf(),
                                user.child("firstName").value.toString(),
                                user.child("userId").value.toString(),
                                user.child("lastName").value.toString(),
                                mutableListOf(),
                                user.child("photoURL").value.toString()
                            )
                            preloadedData.user.value = u
                            Log.d("preloadedData","login: $u")

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
                    //findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                } else {
                    Log.d("RETURN", "showLoginScreen");
                    findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                  //  findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            }
        }, 2000)

        return binding.root
    }


}