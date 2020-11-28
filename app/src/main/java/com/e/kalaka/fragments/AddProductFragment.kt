package com.e.kalaka.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentAddProductBinding
import com.e.kalaka.databinding.FragmentOrderProductBinding
import com.e.kalaka.models.Product
import com.e.kalaka.utils.Validation
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class AddProductFragment : Fragment() {
    //for Firebase
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    //form datas
    private lateinit var name: String
    private lateinit var description: String
    private var price: Double = 0.0

    //helper variables
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var imageUri: Uri
    private val preloadedData: PreloadViewModel by activityViewModels()
    companion object {
        //image pick code
        const val IMAGE_PICK_CODE = 1;
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference


        binding.imageButton.setOnClickListener {
            pickImageFromGallery()

        }

        binding.addProductButton.setOnClickListener {

            val userCredentials = preloadedData.user.value

            name = binding.productNameEditText.text.toString()
            description = binding.descriptionEditText.text.toString()
            price = binding.priceEditText.text.toString().toDouble()
            val businessId = userCredentials?.businessId.toString()
            val productId = UUID.randomUUID().toString()
            if (!isValidForm()) {
                return@setOnClickListener
            }


            val product = Product(businessId,description,name,imageUri.toString(),price,productId)
            writeProductIntoDataBase(product)
        }


        return binding.root

    }

    private fun writeProductIntoDataBase(product: Product) {
        preloadedData.productList
        myRef.child("products").child(product.productId).setValue(product)
    }

    private fun isValidForm(): Boolean {
        if (TextUtils.isEmpty(description)) {
            binding.descriptionEditText.error = "Leírás szükséges"
            return false
        }

        if (TextUtils.isEmpty(price.toString()) || price.toString().toDouble() < 0) {
            binding.priceEditText.error = "Rossz ár"
            return false
        }


        if (TextUtils.isEmpty(name)) {
            binding.productNameEditText.error = "Nincs megadva terméknév"
            return false
        }
        return true

    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RegisterFragment.IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadPicture()
        }
    }

    private fun uploadPicture() {
        val randomKey = UUID.randomUUID().toString()
        val riversRef: StorageReference = storageReference.child("profile_image/" + randomKey)

        riversRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                Log.d("Helo", "kep sikeresen feltoltve")
                Toast.makeText(activity, "Kép sikeresen feltöltve", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("Helo", "valami hiba a kepfeltoltesnel")
            }
    }


}