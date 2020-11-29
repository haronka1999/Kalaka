package com.e.kalaka.fragments

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentDetailsProductBinding
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.storage.FirebaseStorage


class DetailsProductFragment : Fragment() {

    private lateinit var binding: FragmentDetailsProductBinding
    private val preloadedData : PreloadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details_product, container, false)

        val currentProduct = preloadedData.currentProduct
        Log.d("-------", "$currentProduct")

        binding.productNameTextView.text = currentProduct.name.plus(" ").plus(currentProduct.price).plus("RON")
        binding.descriptionTextView.text = currentProduct.description
        setProfileImage(currentProduct.photoURL, binding.businessProfile)

        binding.orderButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailsProductFragment_to_orderProductFragment)
        }


        return binding.root
    }

    private fun setProfileImage(logoURL: String, view: ImageView) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(logoURL)
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            view.setImageBitmap(bmp)
        }
    }


}