package com.godeliveryservices.rider.ui.orders_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OrdersHistoryViewModel : ViewModel() {

    private val apiService = ApiService.create()
    private var disposable: Disposable? = null

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessagePending = MutableLiveData<String>()
    val responseMessagePending: LiveData<String> = _responseMessagePending

    private val _responseMessageProcessing = MutableLiveData<String>()
    val responseMessageProcessing: LiveData<String> = _responseMessageProcessing

    private val _responseMessageDelivered = MutableLiveData<String>()
    val responseMessageDelivered: LiveData<String> = _responseMessageDelivered

    val orderFilters = MutableLiveData<OrderFilters>()

    private val _pendingOrders = MutableLiveData<List<Order>>()
    val pendingOrders: LiveData<List<Order>> = _pendingOrders

    private val _processingOrders = MutableLiveData<List<Order>>()
    val processingOrders: LiveData<List<Order>> = _processingOrders

    private val _deliveredOrders = MutableLiveData<List<Order>>()
    val deliveredOrders: LiveData<List<Order>> = _deliveredOrders

    fun fetchOrders(status: String?, riderId: Long?) {
        _showLoading.value = true
        val filters = orderFilters.value
        disposable = apiService.getOrdersByRider(
            status = status,
            startDate = filters?.startDate,
            endDate = filters?.endDate,
            riderId = riderId
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ success ->
                _showLoading.value = false
                if (success.code() == 200) {
                    when (status) {
                        "Active" -> _processingOrders.value = success.body()
                        "Delivered" -> _deliveredOrders.value = success.body()
                    }
                } else {
                    when (status) {
                        "Active" -> _responseMessageProcessing.value = success.errorBody()?.string()
                        "Delivered" -> _responseMessageDelivered.value =
                            success.errorBody()?.string()
                    }
                    clearOrders(status)
                }
            }, { error ->
                when (status) {
                    "Active" -> _responseMessageProcessing.value =
                        "Please check your internet connection!"
                    "Delivered" -> _responseMessageDelivered.value =
                        "Please check your internet connection!"
                }
                _showLoading.value = false
                clearOrders(status)
            })
    }

    private fun clearOrders(status: String?) {
        when (status) {
            "Pending" -> _pendingOrders.value = listOf()
            "Active" -> _processingOrders.value = listOf()
            "Delivered" -> _deliveredOrders.value = listOf()
        }
    }
}