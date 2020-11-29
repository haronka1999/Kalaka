package com.e.kalaka.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.kalaka.R
import com.e.kalaka.adapters.BusinessAdapter
import com.e.kalaka.databinding.FragmentMainSearchBinding
import com.e.kalaka.models.Business
import com.e.kalaka.viewModels.PreloadViewModel
import com.e.kalaka.viewModels.TopicViewModel


class MainSearch : Fragment(), BusinessAdapter.OnItemClickListener {

    private lateinit var binding : FragmentMainSearchBinding
    private val topicViewModel : TopicViewModel by activityViewModels()
    private val preloadedData : PreloadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_search,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE

        topicViewModel.filteredBusinesslist = topicViewModel.list

        binding.searchInput.addTextChangedListener{
            /*
            val newList = mutableListOf<Business>()
            topicViewModel.list.value?.forEach{ business->
                if(business.name.contains(it.toString())) {
                    newList.add(business)
                }
            }
            topicViewModel.filteredBusinesslist.value = newList
            Log.d("-------", it.toString());
            Log.d("-------", "$newList");
             */
        }
        val adapter = BusinessAdapter(mutableListOf(), this)
        binding.recycleView.adapter = adapter
        binding.recycleView.layoutManager = LinearLayoutManager(context)
        binding.recycleView.setHasFixedSize(true)

        topicViewModel.filteredBusinesslist.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
    }

    override fun onItemClick(position: Int) {
        preloadedData.indicator.value = 2
        preloadedData.searchedBusiness.value = topicViewModel.list.value?.get(position)
        findNavController().navigate(R.id.action_mainSearch_to_businessProfile)
    }
}