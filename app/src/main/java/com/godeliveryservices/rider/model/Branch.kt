package com.godeliveryservices.rider.model

data class Branch(
    val Address: String,
    val Email: String,
    val JoiningDate: String,
    val Mobile: String,
    val Name: String,
    val Orders: Any,
    val Shop: Any,
    val ShopBranchID: Long,
    val ShopID: Long,
    val Status: String
)