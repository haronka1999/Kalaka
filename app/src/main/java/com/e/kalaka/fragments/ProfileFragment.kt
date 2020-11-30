package com.e.kalaka.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.adapters.BusinessAdapter
import com.e.kalaka.databinding.FragmentProfileBinding
import com.e.kalaka.models.Business
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("users")
    var userId = mAuth.currentUser?.uid
    private lateinit var businessId : String
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                businessId =  dataSnapshot.child(userId.toString()).child("businessId").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("abc", "Failed to read value.", error.toException())
            }
        })

    }

    private lateinit var binding: FragmentProfileBinding
    private val preloadedData: PreloadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val view = binding.root
        showDatas()


        binding.myBusiness.setOnClickListener {
            if (businessId == "0") {

                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_profileFragment_to_noBusinessFragment)
            } else {

                preloadedData.indicator.value = 1
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_profileFragment_to_businessProfile)
            }
        }

        //SIGN OUT
        binding.signOut.setOnClickListener {
            mAuth.signOut()
            sharedPreferences = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        return view
    }


    private fun showDatas() {
        val user = preloadedData.user.value
        binding.name.text = "${user?.lastName} ${user?.firstName}"
        binding.email.text= "${user?.email}"
        if (user != null) {
            setProfileImage(user.photoURL, binding.profilePic)
        }
    }

    private fun setProfileImage(logoURL: String, view: ImageView) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(logoURL)
        val FIVE_MEGABYTE = (1024 * 1024 * 5).toLong()
        storageReference.getBytes(FIVE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            view.setImageBitmap(bmp)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }

}