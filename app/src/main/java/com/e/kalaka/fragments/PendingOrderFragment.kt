package com.e.kalaka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.adapters.PendingOrderAdapter
import com.e.kalaka.databinding.FragmentPendingOrderBinding
import com.e.kalaka.databinding.PendingOrderRecyclerItemBinding
import com.e.kalaka.models.BusinessOrder


class PendingOrderFragment : Fragment(), PendingOrderAdapter.OnItemClickListener {

    private lateinit var binding: FragmentPendingOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pending_order, container, false)
        val list = generateDummyList(10)
        val recyclerView = binding.recyclerView
        recyclerView.adapter = PendingOrderAdapter(list, this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    private fun generateDummyList(size: Int): List<BusinessOrder> {
        val list = ArrayList<BusinessOrder>()

//        loadDatas(){
//
//        }

        for (i in 0 until size) {
            val businessOrder =
                BusinessOrder("1", "1", "1", "1",
                    1, "1", "1", "1'", "1", "1", "1", 1.1, "1")
            list += businessOrder
        }
        return list
    }

    override fun onItemClick(position: Int) {
        // TODO
    }
}