package com.e.kalaka.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.e.kalaka.models.Business
import com.e.kalaka.models.Product
import com.e.kalaka.models.User

class PreloadViewModel : ViewModel(){

    var user : MutableLiveData<User> = MutableLiveData<User>()
    var business : MutableLiveData<Business> = MutableLiveData<Business>()
    var productList : MutableLiveData<MutableList<Product>> = MutableLiveData<MutableList<Product>>()
    lateinit var currentProduct: Product
}