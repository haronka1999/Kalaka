package com.e.kalaka.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.adapters.BusinessAdapter
import com.e.kalaka.databinding.FragmentProfileBinding
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
        mAuth = FirebaseAuth.getInstance();
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

        val user = preloadedData.user.value
        showDatas()
        binding.myBusiness.setOnClickListener {


            if (user?.businessId == 0) {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_profileFragment_to_noBusinessFragment)

//                Navigation.findNavController(binding.root)
//                    .navigate(R.id.action_profileFragment_to_businessProfile)

            } else {
                preloadedData.indicator.value = 1
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_profileFragment_to_businessProfile)
            }
        }

        binding.signOut.setOnClickListener {
            mAuth.signOut()
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_profileFragment_to_loginFragment)
        }

        return view
    }


    private fun showDatas() {

        val user = preloadedData.user.value
        binding.firstName.text=user?.firstName
        binding.lastName.text=user?.lastName
        binding.email.text=user?.email
        Log.d("-----", user!!.photoURL)
        setProfileImage(user.photoURL, binding.profilePic)
    }

    private fun setProfileImage(logoURL: String, view: ImageView) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(logoURL)
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            view.setImageBitmap(bmp)
        }
    }

}