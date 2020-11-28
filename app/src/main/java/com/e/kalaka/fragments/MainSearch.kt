package com.e.kalaka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.kalaka.R
import com.e.kalaka.adapters.BusinessAdapter
import com.e.kalaka.databinding.FragmentMainSearchBinding
import com.e.kalaka.viewModels.TopicViewModel


class MainSearch : Fragment(), BusinessAdapter.OnItemClickListener {

    private lateinit var binding : FragmentMainSearchBinding
    private val topicViewModel : TopicViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_search,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topicViewModel.data.observe(viewLifecycleOwner, Observer { list ->
            binding.recycleView.adapter = BusinessAdapter(list, this)
            binding.recycleView.layoutManager = LinearLayoutManager(context)
            binding.recycleView.setHasFixedSize(true)
        })

    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}