package com.e.kalaka.models

data class UserOrder (
    var address : String,
    var city : String,
    var comment : String,
    var number : Int,
    var orderId : Int,
    var postcode : String,
    var productId : Int,
    var productName : String,
    var time : String,
    var total : Double
        )
