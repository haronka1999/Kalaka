package com.e.kalaka.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.kalaka.R
import com.e.kalaka.adapters.ProductAdapter
import com.e.kalaka.databinding.FragmentBusinessProfileBinding
import com.e.kalaka.models.Business
import com.e.kalaka.models.BusinessOrder
import com.e.kalaka.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.e.kalaka.viewModels.PreloadViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class BusinessProfile : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding: FragmentBusinessProfileBinding
    private val preloadedData: PreloadViewModel by activityViewModels()

    //firebase
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var database = FirebaseDatabase.getInstance()
    var myRefBusiness = database.getReference("business")
    var userId = mAuth.currentUser?.uid
    var myRef = database.getReference("users")
    private lateinit var businessId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //get the current user's business
                businessId =
                    dataSnapshot.child(userId.toString()).child("businessId").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("abc", "Failed to read value.", error.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_business_profile,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_businessProfile_to_addProductFragment)
        }

        preloadedData.business.observe(viewLifecycleOwner, Observer {
            val business: Business
            val indicator = preloadedData.indicator.value
            when (indicator) {
                1 -> {
                    business = preloadedData.business.value!!
                }
                2 -> {
                    business = preloadedData.searchedBusiness.value!!
                    hideEditButtons()
                }
                else -> {
                    business = preloadedData.business.value!!
                }
            }

            binding.businessName.text = business.name
            binding.businessDescription.text = business.description
            binding.businessEmail.text = business.email
            binding.businessLabels.text = business.tags.joinToString(", ")
            binding.businessTelephone.text = business.phone
            binding.location.text = business.location
            binding.facebookText.text = if(business.facebookURL.isEmpty()) {
                ""
            } else { business.facebookURL }
            setItemImage(business.logoURL, binding.businessProfile)

            val recycleView = binding.recycleView
            preloadedData.productList.observe(viewLifecycleOwner, Observer {
                    list -> val adapter = indicator?.let { ProductAdapter(list, this, requireActivity(), it) }
                recycleView.adapter = adapter
                val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recycleView.layoutManager = horizontalLayout
                recycleView.setHasFixedSize(true)
            })
            loadProducts(business.productIds, business.businessId)
        })

        loadBusiness(userId)
    }

    override fun onItemClick(position: Int) {
        preloadedData.currentProduct = preloadedData.productList.value!![position]
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_businessProfile_to_detailsProductFragment)
    }

    private fun hideEditButtons() {
        binding.editBusiness.visibility = View.GONE
        binding.addButton.visibility = View.GONE
        binding.statisticsButton.visibility = View.GONE
    }

    private fun loadProducts(list: List<String>, id: String) {
        database.getReference("products").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Product>()

                for (product in snapshot.children) {
                    if (id == product.child("businessId").value.toString())
                        list.add(
                            Product(
                                product.child("businessId").value.toString(),
                                product.child("description").value.toString(),
                                product.child("name").value.toString(),
                                product.child("photoURL").value.toString(),
                                product.child("price").value.toString().toDouble(),
                                product.child("productId").value.toString()
                            )
                        )

                }
                preloadedData.productList.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadBusiness(userId: String?) {
        myRefBusiness.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //search for the user's business
                for (business in dataSnapshot.children) {
                    Log.d("BUSINESS", "$business")
                    if (businessId == business.key) {
                        val myBusinessId =
                            dataSnapshot.child(businessId).child("businessId").value.toString()
                        val description =
                            dataSnapshot.child(businessId).child("description").value.toString()
                        val email = dataSnapshot.child(businessId).child("email").value.toString()
                        val facebookURL =
                            dataSnapshot.child(businessId).child("facebookURL").value.toString()
                        val instagramURL =
                            dataSnapshot.child(businessId).child("instagramURL").value.toString()
                        val location =
                            dataSnapshot.child(businessId).child("location").value.toString()
                        val logoURL =
                            dataSnapshot.child(businessId).child("logoURL").value.toString()
                        val name = dataSnapshot.child(businessId).child("name").value.toString()
                        val ownerId =
                            dataSnapshot.child(businessId).child("ownerId").value.toString()
                        val phone = dataSnapshot.child(businessId).child("phone").value.toString()

                        //TODO: push businessOrder
                        val EmptyorderList: MutableList<BusinessOrder> = arrayListOf()
                        val business = Business(
                            businessId,
                            description,
                            email,
                            facebookURL,
                            instagramURL,
                            location,
                            logoURL,
                            mutableListOf("member1", "member2"),
                            name,
                            EmptyorderList,
                            ownerId,
                            phone,
                            listOf(),
                            listOf("tag1", "tag2")
                        )
                        preloadedData.business.value = business
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("abc", "Failed to read value.", error.toException())
            }
        })


    }

    private fun setItemImage(logoURL: String, holder: ImageView) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child(logoURL)
        val ONE_MEGABYTE = (500 * 500).toLong()
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            holder.setImageBitmap(bmp)
        }
    }
}