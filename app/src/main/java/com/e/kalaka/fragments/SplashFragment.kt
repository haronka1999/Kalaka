package com.e.kalaka.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentRegisterBinding
import com.e.kalaka.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class SplashFragment : Fragment() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSplashBinding

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
                    Log.d("RETURN", "mainScreen");
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                } else {
                    Log.d("RETURN", "showLoginScreen");
//                    findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            }
        }, 2000)

        return binding.root
    }


}