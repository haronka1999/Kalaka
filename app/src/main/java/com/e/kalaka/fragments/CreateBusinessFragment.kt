package com.e.kalaka.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentCreateBusinessBinding
import com.e.kalaka.models.Business
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.utils.Tag
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class CreateBusinessFragment : Fragment() {

    private lateinit var binding: FragmentCreateBusinessBinding
    private var tags = mutableListOf<String>()
    private val emails= mutableListOf<String>()
    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserEmails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_business,
            container,
            false
        )

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        //setupAutoCompleteView()
        binding.chooseLogoButton.setOnClickListener{
            pickImageFromGallery()
        }

        binding.createBusinessButton.setOnClickListener{
            val name = binding.createBusinessNameText.toString()
            val email = binding.createBusinessEmailText.text.toString()
            val phoneNumber = binding.createBusinessPhoneText.text.toString()
            val address = binding.createBusinessAddressText.text.toString()
            val description = binding.createBusinessDescriptionText.text.toString()
            val fbAddress = binding.createBusinessFbText.text.toString()
            val instaAddress = binding.createBusinessInstaText.text.toString()

            if(!businessValidation(name, email, phoneNumber, address, description)) {
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val logoUri: String = if(imageUri == null) {
                ""
            } else {
                imageUri.toString()
            }

            val randomKey = UUID.randomUUID().toString()
            val business = Business(randomKey,
                                    description,
                                    email,
                                    fbAddress,
                                    instaAddress,
                                    address,
                                    logoUri,
                                    listOf(userId!!) as MutableList<String>,
                                    name,
                                    listOf<BusinessOrder>() as MutableList<BusinessOrder>,
                                    userId,
                                    phoneNumber,
                                    listOf(),
                                    tags
                                    )
            uploadBusiness(business)

        }

        setUpSpinner()

        binding.createBusinessTagContainer.removeAllViews()

        return binding.root
    }

    private fun uploadBusiness(business: Business) {
        //uploadPicture()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference
        myRef.child("users").child(business.businessId).setValue(business)
    }

    private fun businessValidation(name: String, email: String, phoneNumber: String, address: String, description: String): Boolean {
        if(name.isEmpty()) {
            Toast.makeText(binding.root.context, "A nev tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if(email.isEmpty()) {
            Toast.makeText(binding.root.context, "Az email tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if(phoneNumber.isEmpty()) {
            Toast.makeText(binding.root.context, "A telefonszam tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if(address.isEmpty()) {
            Toast.makeText(binding.root.context, "A cim tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if(description.isEmpty()) {
            Toast.makeText(binding.root.context, "A leiras tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        /*
        if(imageUri == null) {
            Toast.makeText(binding.root.context, "Valasszon kepet", Toast.LENGTH_SHORT).show()
            return false
        }*/
        return true
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadPicture()
            //Log.d("------", imageUri.toString())
        }
    }

    private fun uploadPicture() {
        val randomKey = UUID.randomUUID().toString()
        val riversRef: StorageReference = storageReference.child("business_image/$randomKey")

        imageUri?.let {
            riversRef.putFile(it)
                .addOnSuccessListener {   // Get a URL to the uploaded content
                    Log.d("Helo", "kep sikeresen feltoltve")
                    Toast.makeText(activity, "Kép sikeresen feltöltve", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.d("Helo", "valami hiba a kepfeltoltesnel")
                }
        }
    }

    private fun setupAutoCompleteView() {
        val autoComplete = binding.userAutoComplete
        val adapter = ArrayAdapter(binding.root.context, R.layout.spinner_item_layout, emails)
        autoComplete.setAdapter(adapter)
        autoComplete.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val email = emails[position]
                addUserChip(email)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun addUserChip(email: Any) {

    }

    private fun getUserEmails() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val email = data.child("email").value.toString()
                    emails.add(email)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("--------", "Failed to read value.", error.toException())
            }
        })
    }

    private fun setUpSpinner() {
        val spinner = binding.tagSpinner
        val arrayAdapter = ArrayAdapter(
            binding.root.context,
            R.layout.spinner_item_layout,
            Tag.getTagList()
        )
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item  = Tag.getTags()[position]
                if(!tags.contains(item.second)) {
                    tags.add(item.second)
                    addTagChip(item)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }

    private fun addTagChip(item: Pair<String, String>) {
        val chipGroup = binding.createBusinessTagContainer
        val chip = Chip(binding.root.context)
        val paddingDp: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10F,
            resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = item.second
        val iconId = binding.root.context.resources.getIdentifier(
            item.first,
            "drawable",
            binding.root.context.packageName
        )
        chip.chipIcon = ContextCompat.getDrawable(binding.root.context, iconId)
        chip.isCloseIconVisible = true
        chip.setCloseIconResource(R.drawable.ic_action_close)
        chip.setOnCloseIconClickListener {
            it as TextView
            tags.remove(it.text)
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

}