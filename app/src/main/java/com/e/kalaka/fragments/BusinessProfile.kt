package com.e.kalaka.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.adapters.BusinessProfileAdapter
import com.e.kalaka.databinding.FragmentBusinessProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.e.kalaka.viewModels.PreloadViewModel


class BusinessProfile : Fragment(), BusinessProfileAdapter.OnItemClickListener {

    private lateinit var binding : FragmentBusinessProfileBinding
    private val preloadedData : PreloadViewModel by activityViewModels()

    private lateinit var mAuth: FirebaseAuth
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
        binding.businessDescription.text = business?.description
        binding.businessEmail.text = business?.email
        binding.businessLabels.text = business?.tags?.joinToString(", ")
        binding.businessTelephone.text = business?.phone
        binding.location.text = business?.location
        Glide.with(this).load(business?.logoURL).into(binding.businessProfile)


        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        val recycle_view = binding.recycleView


        preloadedData.productList.observe(viewLifecycleOwner, Observer { list ->
            val adapter = BusinessProfileAdapter(list, this)
            recycle_view.adapter = adapter
            val HorizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recycle_view.layoutManager = HorizontalLayout
            recycle_view.setHasFixedSize(true)
        })



    }

    override fun onItemClick(position: Int) {
       // TODO("Not yet implemented")
        //helo
    }




}