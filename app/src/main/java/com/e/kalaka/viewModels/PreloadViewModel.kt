package com.e.kalaka.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.e.kalaka.models.Business
import com.e.kalaka.models.Product
import com.e.kalaka.models.User
import com.e.kalaka.models.UserOrder

class PreloadViewModel : ViewModel(){

    var user : MutableLiveData<User> = MutableLiveData<User>()

    //profilnak
    var business : MutableLiveData<Business> = MutableLiveData<Business>()

    //listazasbol
    var searchedBusiness : MutableLiveData<Business> = MutableLiveData<Business>()


    /*
    an integer indicator from where the user goes to the business profile
    1 - preloaded, which is the user's own business
    2 - searched business (the user doesn't own it)
     */
    var indicator : MutableLiveData<Int> = MutableLiveData<Int>()


    //this will be initalized in the
    var pendingOrders: MutableLiveData<MutableList<UserOrder>> = MutableLiveData<MutableList<UserOrder>>()

    var productList : MutableLiveData<MutableList<Product>> = MutableLiveData<MutableList<Product>>()
    var favoriteProductlist: MutableLiveData<MutableList<Product>> = MutableLiveData<MutableList<Product>>()
    var userEmails: MutableLiveData<List<Pair<String,String>>> = MutableLiveData<List<Pair<String,String>>>()
    lateinit var currentProduct: Product
}