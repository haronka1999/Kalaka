package com.e.kalaka.fragments

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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.adapters.ProductAdapter
import com.e.kalaka.databinding.FragmentBusinessProfileBinding
import com.e.kalaka.models.Business
import com.e.kalaka.models.User
import com.google.firebase.auth.FirebaseAuth
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.storage.FirebaseStorage


class BusinessProfile : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding: FragmentBusinessProfileBinding
    private val preloadedData: PreloadViewModel by activityViewModels()
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE

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

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_businessProfile_to_addProductFragment)
        }

        var business : Business
        val indicator = preloadedData.indicator.value
        when(indicator){
            1 -> {
                business = preloadedData.business.value!!
            }
            2 -> {
                business = preloadedData.searchedBusiness.value!!
                hideEditButtons()
            }
            else -> {
                business= preloadedData.business.value!!
            }
        }

        binding.businessName.text = business?.name
        binding.businessDescription.text = business?.description
        binding.businessEmail.text = business?.email
        binding.businessLabels.text = business?.tags?.joinToString(", ")
        binding.businessTelephone.text = business?.phone
        binding.location.text = business?.location
        Glide.with(this).load(Uri.parse(business?.logoURL))
            .circleCrop()
            .into(binding.businessProfile)



        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
        val recycle_view = binding.recycleView


        preloadedData.productList.observe(viewLifecycleOwner, Observer { list ->
            val adapter = indicator?.let { ProductAdapter(list, this, requireActivity(), it) }
            recycle_view.adapter = adapter
            val HorizontalLayout =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recycle_view.layoutManager = HorizontalLayout
            recycle_view.setHasFixedSize(true)
        })

    }

    override fun onItemClick(position: Int) {
        preloadedData.currentProduct = preloadedData.productList.value!![position]
    }

    private fun hideEditButtons(){
        binding.editBusiness.visibility = View.GONE
        binding.addButton.visibility = View.GONE
        binding.statisticsButton.visibility = View.GONE
        //binding.recycleView.findViewById<ImageView>(R.id.favorite_product).visibility = View.GONE
       // binding.recycleView.findViewById<ImageView>(R.id.delete_product).visibility = View.GONE
    }
}