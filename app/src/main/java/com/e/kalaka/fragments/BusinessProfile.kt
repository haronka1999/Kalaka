package com.e.kalaka.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.adapters.ProductAdapter
import com.e.kalaka.databinding.FragmentBusinessProfileBinding
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.storage.FirebaseStorage


class BusinessProfile : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding : FragmentBusinessProfileBinding
    private val preloadedData : PreloadViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = DataBindingUtil.inflate(
           inflater,
           R.layout.fragment_business_profile,
           container,
           false
       )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val business = preloadedData.business.value
        binding.businessName.text = business?.name
        binding.businessDescription.text = business?.description
        binding.businessEmail.text = business?.email
        binding.businessLabels.text = business?.tags?.joinToString(", ")
        binding.businessTelephone.text = business?.phone
        binding.location.text = business?.location
        Glide.with(this).load(Uri.parse(business?.logoURL))
            .circleCrop()
            .into(binding.businessProfile)



        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        val recycle_view = binding.recycleView


        preloadedData.productList.observe(viewLifecycleOwner, Observer { list ->
            val adapter = ProductAdapter(list, this, requireActivity())
            recycle_view.adapter = adapter
            val HorizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recycle_view.layoutManager = HorizontalLayout
            recycle_view.setHasFixedSize(true)
        })

    }

    override fun onItemClick(position: Int) {
        preloadedData.currentProduct = preloadedData.productList.value!![position]
    }
}