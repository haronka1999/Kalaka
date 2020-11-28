package com.e.kalaka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.kalaka.R
import com.e.kalaka.adapters.BusinessProfileAdapter
import com.e.kalaka.databinding.FragmentBusinessProfileBinding


class BusinessProfile : Fragment(), BusinessProfileAdapter.OnItemClickListener {

    private lateinit var binding : FragmentBusinessProfileBinding

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

        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        val recycle_view = binding.recycleView


        val adapter = BusinessProfileAdapter(mutableListOf(1, 2), this)
        recycle_view.adapter = adapter

        val HorizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recycle_view.layoutManager = HorizontalLayout

        recycle_view.setHasFixedSize(true)


    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }




}