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
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.e.kalaka.R
import com.e.kalaka.databinding.FragmentCreateBusinessBinding
import com.e.kalaka.models.Business
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.utils.Tag
import com.e.kalaka.viewModels.PreloadViewModel
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
    private var members = mutableListOf<String>()
    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage
    private var  userId  = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var storageReference: StorageReference
    private val preloadedData: PreloadViewModel by activityViewModels()
    private val preloadedUserData: PreloadViewModel by activityViewModels()
    private lateinit var emails: List<Pair<String, String>>

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   emails = preloadedData.userEmails.value!!
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

      //  setupAutoCompleteView()

        binding.chooseLogoButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.createBusinessButton.setOnClickListener {
            val name = binding.createBusinessNameText.text.toString()
            val email = binding.createBusinessEmailText.text.toString()
            val phoneNumber = binding.createBusinessPhoneText.text.toString()
            val address = binding.createBusinessAddressText.text.toString()
            val description = binding.createBusinessDescriptionText.text.toString()
            val fbAddress = binding.createBusinessFbText.text.toString()
            val instaAddress = binding.createBusinessInstaText.text.toString()


            if (!businessValidation(name, email, phoneNumber, address, description)) {
                return@setOnClickListener
            }

           //  userId = FirebaseAuth.getInstance().currentUser?.uid

            val logoUri: String = if (imageUri == null) {
                ""
            } else {
                imageUri.toString()
            }

            val randomKey = UUID.randomUUID().toString()
            val logoPath: String = "business_image/$randomKey"
            val EmptyorderList : MutableList<BusinessOrder> = arrayListOf()
            val business = Business(randomKey,
                                    description,
                                    email,
                                    fbAddress,
                                    instaAddress,
                                    address,
                                    logoPath,
                                    members,
                                    name,
                                    EmptyorderList,
                                    userId!!,
                                    phoneNumber,
                                    listOf(),
                                    tags
                                    )
            uploadPicture(randomKey)
            uploadBusiness(business)
            updateOwner(business)
            updateMembers(business)

            preloadedUserData.user.value?.businessId = business.businessId
           // preloadedData.business.value = business
            Log.d("abc","userid creat ${preloadedUserData.user.value?.businessId}")
            Toast.makeText(
                binding.root.context,
                "Sikeresen létrehozta a vállalkozást",
                Toast.LENGTH_SHORT
            ).show()
            preloadedData.business.value = business
            preloadedData.indicator.value = 1
            Toast.makeText(binding.root.context, "Sikeresen létrehozta a vállalkozást", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(requireView()).navigate(R.id.businessProfile)
        }

        setUpSpinner()

        return binding.root
    }

    private fun updateMembers(business: Business) {
        if (business.memberIds.size != 0) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users")
            Log.d("abc","elotte")
            userRef.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("abc","benne")
                    for (uId in business.memberIds) {
                        Log.d("abc","uId: $uId")
                        for (user in dataSnapshot.children) {
                            Log.d("abc","user1: $user")
                            if (user.key.toString() == uId) {
                                Log.d("abc","user2: $user")
                                user.child("businessId").ref.setValue(business.businessId)
                                break;
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(
                        "TAG",
                        "Failed to read value.",
                        error.toException()
                    )
                }
            })
        }
    }

    private fun updateOwner(business: Business) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users")
        userRef.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (user in dataSnapshot.children) {
                    if (user.key.toString() == business.ownerId) {
                        user.child("businessId").ref.setValue(business.businessId)
                        break;
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(
                    "TAG",
                    "Failed to read value.",
                    error.toException()
                )
            }
        })

    }

    private fun uploadBusiness(business: Business) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference

        myRef.child("business").child(business.businessId).setValue(business)
        myRef.child("users").child(userId.toString()).child("businessId").setValue(business.businessId)
    }

    private fun businessValidation(
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        description: String
    ): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(binding.root.context, "A nev tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty()) {
            Toast.makeText(binding.root.context, "Az email tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(binding.root.context, "A telefonszam tul rovid", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (address.isEmpty()) {
            Toast.makeText(binding.root.context, "A cim tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isEmpty()) {
            Toast.makeText(binding.root.context, "A leiras tul rovid", Toast.LENGTH_SHORT).show()
            return false
        }

        if(imageUri == null) {
            Toast.makeText(binding.root.context, "Válasszon képet", Toast.LENGTH_SHORT).show()
            return false
        }
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
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
        }
    }

    private fun uploadPicture(key: String) {
        val riversRef: StorageReference = storageReference.child("business_image/$key")

        imageUri?.let {
            riversRef.putFile(it)
                .addOnSuccessListener {   // Get a URL to the uploaded content
                    //Log.d("Helo", "kep sikeresen feltoltve")
                    //Toast.makeText(activity, "Kép sikeresen feltöltve", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.d("Helo", "valami hiba a kepfeltoltesnel")
                }
        }
    }

    private fun setupAutoCompleteView() {
        val userEmails = mutableListOf<String>()
//        emails.forEach {
//            userEmails.add(it.second)
//        }

        val autoComplete = binding.userAutoComplete
        val adapter = ArrayAdapter(
            binding.root.context, R.layout.support_simple_spinner_dropdown_item,
            userEmails
        )
        autoComplete.setAdapter(adapter)
        autoComplete.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val email = emails[position]
                if (!members.contains(email.first)) {
                    autoComplete.hint = ""
                    members.add(email.first)
                    addUserChip(email)
                }
            }
        }
    }

    private fun addUserChip(email: Pair<String, String>) {
        val chipGroup = binding.createBusinessMemberContainer
        val chip = Chip(binding.root.context)
        val paddingDp: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10F,
            resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = email.second
        chip.isCloseIconVisible = true
        chip.setCloseIconResource(R.drawable.ic_action_close)
        chip.setOnCloseIconClickListener {
            it as TextView
            val userid = emails.filter { (_, value) -> value == it.text }
            members.remove(userid[0].first)
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

    private fun setUpSpinner() {
        val spinner = binding.tagSpinner
        val arrayAdapter = ArrayAdapter(
            binding.root.context,
            R.layout.spinner_item_layout,
            Tag.getTagList()
        )
        spinner.adapter = arrayAdapter

        spinner.setSelection(0, false)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = Tag.getTags()[position]
                if (!tags.contains(item.second)) {
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