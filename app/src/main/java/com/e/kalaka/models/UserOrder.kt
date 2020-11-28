package com.e.kalaka.models

data class UserOrder (
    var address : String,
    var city : String,
    var clientId: String,
    var comment : String,
    var number : String,
    var orderId : String,
    var postcode : String,
    var productId : String,
    var productName : String,
    var time : String,
    var total : Double
        )
