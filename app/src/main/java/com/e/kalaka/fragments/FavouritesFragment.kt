package com.e.kalaka.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.kalaka.R
import com.e.kalaka.adapters.FavoriteListAdapter
import com.e.kalaka.databinding.FragmentFavouritesBinding
import com.e.kalaka.viewModels.PreloadViewModel

class FavouritesFragment : Fragment(), FavoriteListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var recyclerView: RecyclerView
    private val preloadedData : PreloadViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)

        if (preloadedData.favoriteProductlist.value == null) {
            binding.favoritesTitleText.visibility = View.VISIBLE
        }

        recyclerView = binding.favoritesRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        val adapter = FavoriteListAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter

        preloadedData.favoriteProductlist.observe(viewLifecycleOwner) { products ->
            adapter.setData(products)
        }

        return binding.root
    }

    override fun onItemClick(position: Int) {
        preloadedData.currentProduct = preloadedData.favoriteProductlist.value!![position]
        Navigation.findNavController(requireView()).navigate(R.id.detailsProductFragment)
    }

}