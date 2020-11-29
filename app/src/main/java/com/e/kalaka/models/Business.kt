package com.e.kalaka.models

data class Business(
    var businessId: String,
    var description: String,
    var email: String,
    var facebookURL: String,
    var instagramURL: String,
    var location: String,
    var logoURL: String,
    var memberIds: MutableList<String>,
    var name: String,
    var orders: MutableList<BusinessOrder>,
    var ownerId: String,
    var phone: String,
    var productIds: List<String>,
    var tags: List<String>
    )