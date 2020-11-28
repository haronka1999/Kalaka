package com.e.kalaka.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentOrderProductBinding
import com.e.kalaka.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class OrderProductFragment : Fragment() {
    //for realtime database
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference

    //form datas
    private lateinit var number: String
    private lateinit var city: String
    private lateinit var address: String
    private lateinit var postalCode: String
    private lateinit var quantity: String

    //helper variables
    private lateinit var price: String
    private lateinit var binding: FragmentOrderProductBinding
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_product, container, false)


        return binding.root
    }

}