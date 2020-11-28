package com.e.kalaka.models

data class Business (
    var businessId : Int,
    var description : String,
    var email : String,
    var facebookURL : String,
    var instagramURL : String,
    var location : String,
    var logoURL : String,
    var memberIds : MutableList<Int>,
    var name : String,
    var orders : MutableList<BusinessOrder>,
    var ownerId : Int,
    var phone : String,
    var productIds : List<Int>,
    var tags : List<String>
    )