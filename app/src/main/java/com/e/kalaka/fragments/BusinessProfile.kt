package com.e.kalaka.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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


class BusinessProfile : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var binding: FragmentBusinessProfileBinding
    private val preloadedData: PreloadViewModel by activityViewModels()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var myRefUsers = FirebaseDatabase.getInstance().getReference("users")
    var myRefBusiness = FirebaseDatabase.getInstance().getReference("business")
    var myRefProducts = FirebaseDatabase.getInstance().getReference("products")
    var userId = mAuth.currentUser?.uid
    private var lateinit business : Business()
    private  var businessId : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
        myRefUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //get the current user's businessId
                businessId = dataSnapshot.child(userId.toString()).child("businessId").value.toString()
                Log.d("Helo", "businessID in oncreate : $businessId")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("abc", "Failed to read value.", error.toException())
            }
        })

        myRefBusiness.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //search for the user's business
                for (business in dataSnapshot.children) {
                    Log.d("Helo", "business.key  in oncreateview : ${business.key}")
                    if (businessId == business.key) {
                        Log.d("Helo", "Megvagy")
                        val businessId = dataSnapshot.child(businessId).child("businessId").value.toString()
                        Log.d("Helo", "businessID in oncreateview : $businessId")
                        val description = dataSnapshot.child(businessId).child("description").value.toString()
                        val email = dataSnapshot.child(businessId).child("email").value.toString()
                        val facebookURL = dataSnapshot.child(businessId).child("facebookURL").value.toString()
                        val instagramURL = dataSnapshot.child(businessId).child("instagramURL").value.toString()
                        val location = dataSnapshot.child(businessId).child("location").value.toString()
                        val logoURL = dataSnapshot.child(businessId).child("logoURL").value.toString()
                        val name = dataSnapshot.child(businessId).child("name").value.toString()
                        val ownerId = dataSnapshot.child(businessId).child("ownerId").value.toString()
                        val phone = dataSnapshot.child(businessId).child("phone").value.toString()
                        val EmptyorderList : MutableList<BusinessOrder> = arrayListOf()
                         business = Business(
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
                            listOf("ds", "sds"),
                            listOf("tag1", "tag2")
                        )
                      //  myRefBusiness.child(userId.toString()).setValue(business)
                        Log.d("Helo", "Business: $business")
                    }
                }
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
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_business_profile,
            container,
            false
        )

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_businessProfile_to_addProductFragment)


        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //        val indicator = preloadedData.indicator.value
//        when(indicator){
//            1 -> {
//                business = preloadedData.business.value!!
//            }
//            2 -> {
//                business = preloadedData.searchedBusiness.value!!
//                hideEditButtons()
//            }
//            else -> {
//                business= preloadedData.business.value!!
//            }
//        }


        // var business : Business = preloadedData.business.value!!
//       Log.d("Helo", "ownerId: " + business.ownerId)
//        binding.businessName.text = business?.name
//        binding.businessDescription.text = business?.description
//        binding.businessEmail.text = business?.email
//        binding.businessLabels.text = business?.tags?.joinToString(", ")
//        binding.businessTelephone.text = business?.phone
//        binding.location.text = business?.location
//        Glide.with(this).load(Uri.parse(business?.logoURL))
//            .circleCrop()
//            .into(binding.businessProfile)



        val recycle_view = binding.recycleView


//        preloadedData.productList.observe(viewLifecycleOwner, Observer { list ->
//            val adapter = indicator?.let { ProductAdapter(list, this, requireActivity(), it) }
//            recycle_view.adapter = adapter
//            val HorizontalLayout =
//                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            recycle_view.layoutManager = HorizontalLayout
//            recycle_view.setHasFixedSize(true)
//        })
        // loadProducts(business.productIds, business.businessId)


    }

    override fun onItemClick(position: Int) {
        preloadedData.currentProduct = preloadedData.productList.value!![position]
    }

    private fun hideEditButtons() {
        binding.editBusiness.visibility = View.GONE
        binding.addButton.visibility = View.GONE
        binding.statisticsButton.visibility = View.GONE
    }

    private fun loadProducts(list: List<String>, id: String) {
        //database = FirebaseDatabase.getInstance()

        myRefProducts.addValueEventListener(object : ValueEventListener {
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
}