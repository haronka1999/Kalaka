package com.e.kalaka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentDetailsProductBinding


class DetailsProductFragment : Fragment() {

    private lateinit var binding: FragmentDetailsProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_details_product, container, false)

        binding.orderButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailsProductFragment_to_orderProductFragment)
        }


        return binding.root
    }


}