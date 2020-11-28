package com.e.kalaka.models

data class User (
    var businessId : Int,
    var email : String,
    var favorites : MutableList<Int>,
    var firstName : String,
    var userId : String,
    var lastName : String,
    var orders : MutableList<UserOrder>,
    var photoURL : String
        )