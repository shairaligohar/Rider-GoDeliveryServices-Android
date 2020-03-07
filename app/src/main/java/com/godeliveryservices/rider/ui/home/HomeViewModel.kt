package com.godeliveryservices.rider.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _riderPendingOrders = MutableLiveData<List<Order>>()
    val riderPendingOrders: LiveData<List<Order>> = _riderPendingOrders

    private val _riderActiveOrders = MutableLiveData<List<Order>>()
    val riderActiveOrders: LiveData<List<Order>> = _riderActiveOrders

    private val apiService = ApiService.create()
    private var disposable: Disposable? = null

//    var riderId: Long? = null

    fun fetchOrders(riderId: Long?) {
        fetchPendingOrders(riderId)
    }

    private fun fetchPendingOrders(riderId: Long?) {
        _showLoading.value = true
        _riderPendingOrders.value = listOf()
        disposable = apiService.fetchRiderOrders(riderId, "Pending")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { orders ->
                    _showLoading.value = false
                    _riderPendingOrders.value = orders
                    fetchActiveOrders(riderId)
                },
                { error ->
                    _showLoading.value = false
                    _responseMessage.value = error.message
                    fetchActiveOrders(riderId)
                }
            )
    }

    private fun fetchActiveOrders(riderId: Long?) {
        _showLoading.value = true
        _riderActiveOrders.value = listOf()
        disposable = apiService.fetchRiderOrders(riderId, "Active")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { orders ->
                    _showLoading.value = false
                    _riderActiveOrders.value = orders
                },
                { error ->
                    _showLoading.value = false
                    _responseMessage.value = error.message
                }
            )
    }

    fun updateOrderStatus(order: Order) {
        _showLoading.value = true
        var flag = "Pending"
        when (order.Status) {
            "Pending" -> flag = "Accepted"
            "Accepted" -> flag = "Picked Up"
            "Picked Up" -> flag = "Delivered"
        }
        disposable = apiService.updateOrderStatus(
            orderId = order.OrderID,
            riderId = order.RiderID,
            flag = flag
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { success ->
                    _showLoading.value = false
                    fetchOrders(order.RiderID)
                    if (!success.errorBody()?.string().isNullOrBlank())
                        _responseMessage.value = success.errorBody()?.string()
                },
                { error ->
                    _showLoading.value = false
                    fetchOrders(order.RiderID)
                    _responseMessage.value = error.message
                }
            )
    }
}