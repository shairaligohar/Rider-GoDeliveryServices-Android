package com.godeliveryservices.rider.network

import com.godeliveryservices.rider.model.Order

open class APIResponse(val status: Int, val message: String?)

class RiderOrdersResponse(val data: List<Order>)