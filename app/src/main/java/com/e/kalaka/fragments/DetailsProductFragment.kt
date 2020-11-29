package com.e.kalaka.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentDetailsProductBinding
import com.e.kalaka.viewModels.PreloadViewModel


class DetailsProductFragment : Fragment() {

    private lateinit var binding: FragmentDetailsProductBinding
    private val preloadedData : PreloadViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details_product, container, false)

        val currentProduct = preloadedData.currentProduct

        binding.productNameTextView.text = currentProduct.name.plus(" ").plus(currentProduct.price).plus("RON")
        binding.descriptionTextView.text = currentProduct.description
        context?.let {
            Glide.with(it)
                .load(currentProduct.photoURL)
                .into(binding.businessProfile)
        };

        binding.orderButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailsProductFragment_to_orderProductFragment)
        }


        return binding.root
    }


}