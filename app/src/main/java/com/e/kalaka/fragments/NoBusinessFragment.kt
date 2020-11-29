package com.e.kalaka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentNoBusinessBinding

class NoBusinessFragment : Fragment() {

    private lateinit var binding: FragmentNoBusinessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_no_business, container, false)

        binding.createBusinessButton.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(R.id.createBusinessFragment)
        }

        return binding.root
    }

}