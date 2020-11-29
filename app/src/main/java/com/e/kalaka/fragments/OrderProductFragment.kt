package com.e.kalaka.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentOrderProductBinding
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.models.UserOrder
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class OrderProductFragment : Fragment() {
    //for realtime database
    var database = FirebaseDatabase.getInstance()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    //form datas
    private lateinit var number: String
    private lateinit var city: String
    private lateinit var address: String
    private lateinit var postalCode: String
    private lateinit var comment: String
    private var price = 0.0
    private var quantity = 0

    //helper variables
    private lateinit var binding: FragmentOrderProductBinding
    private lateinit var userId: String

    private val preloadedData : PreloadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_product, container, false)

        binding.quantityPicker.minValue = 0
        binding.quantityPicker.maxValue = 10
        binding.quantityPicker.wrapSelectorWheel = true

        val currentProduct = preloadedData.currentProduct
        val productPrice = currentProduct.price

        binding.quantityPicker.setOnValueChangedListener{ _, _, value ->
            quantity = value
            binding.price.text = "Összeg: ${quantity*productPrice} RON"
        }

        binding.orderButton.setOnClickListener{
            number = binding.telEditText.text.toString()
            city = binding.cityEditText.text.toString()
            address = binding.addressEditText.text.toString()
            postalCode = binding.postalCodeEditText.text.toString()
            comment = binding.commentEditText.text.toString()


            val businessId = currentProduct.businessId
            val productName = currentProduct.name
            val productId = currentProduct.productId

            if (!validateOrder(number, city, address, postalCode)) {
                    return@setOnClickListener
            }

            if (quantity == 0) {
                Toast.makeText(binding.root.context, "Minimum egy terméket tud rendelni", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val productPrice = currentProduct.price
            price = quantity*productPrice
            binding.price.text=price.toString()
            val randomKey = UUID.randomUUID().toString()
            val currentTime = SimpleDateFormat("YYYY.MM.DD").toString()

            userId = mAuth.currentUser?.uid.toString()


            val userOrder = UserOrder(address, city, userId, comment, number, randomKey, postalCode, productId, productName, currentTime, price)
            val businessOrder = BusinessOrder(address, city, userId, comment, number, randomKey, postalCode, productId, productName, 0 , currentTime, price, "")
            uploadOrder(userOrder, businessOrder, businessId)
        }
        return binding.root
    }

    private fun uploadOrder(order: UserOrder, businessOrder: BusinessOrder, businessId: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        myRef.child("business").child(businessId).child("orders").child(order.orderId).setValue(businessOrder)
        myRef.child("users").child(userId).child("orders").child(order.orderId).setValue(order)
        Toast.makeText(activity, "Rendelés sikeresen leadva!", Toast.LENGTH_SHORT).show()
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_orderProductFragment_to_homeFragment)
    }

    private fun validateOrder(number: String, city: String, address: String, postalCode: String): Boolean {
        if(number.isEmpty()) {
            Toast.makeText(binding.root.context, "A telefonszam tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if(city.isEmpty()) {
            Toast.makeText(binding.root.context, "A varos tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if(address.isEmpty()) {
            Toast.makeText(binding.root.context, "A cim tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if(postalCode.isEmpty()) {
            Toast.makeText(binding.root.context, "A postakod tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}