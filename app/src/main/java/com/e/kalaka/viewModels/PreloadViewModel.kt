package com.e.kalaka.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.e.kalaka.models.*

class PreloadViewModel : ViewModel(){

    var user : MutableLiveData<User> = MutableLiveData<User>()

    // profilnak
    var business : MutableLiveData<Business> = MutableLiveData<Business>()

    // listazasbol
    var searchedBusiness : MutableLiveData<Business> = MutableLiveData<Business>()


    /*
    an integer indicator from where the user goes to the business profile
    1 - preloaded, which is the user's own business
    2 - searched business (the user doesn't own it)
     */
    var indicator : MutableLiveData<Int> = MutableLiveData<Int>()


    //this will be initalized in the
    var pendingOrders: MutableLiveData<MutableList<BusinessOrder>> = MutableLiveData<MutableList<BusinessOrder>>()
    lateinit var currentPendingOrder: BusinessOrder

    var productList : MutableLiveData<MutableList<Product>> = MutableLiveData<MutableList<Product>>()
    var favoriteProductlist: MutableLiveData<MutableList<Product>> = MutableLiveData<MutableList<Product>>()
    var userEmails: MutableLiveData<MutableList<Pair<String,String>>> = MutableLiveData<MutableList<Pair<String,String>>>()
    lateinit var currentProduct: Product
    var businessName : MutableLiveData<String> = MutableLiveData<String>()
}