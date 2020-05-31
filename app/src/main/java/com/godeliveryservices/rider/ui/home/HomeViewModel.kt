package com.godeliveryservices.rider.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.internal.http2.Http2
import retrofit2.HttpException
import retrofit2.http.HTTP

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _showLoading = MutableLiveData(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _riderPendingOrders = MutableLiveData<List<Order>>()
    val riderPendingOrders: LiveData<List<Order>> = _riderPendingOrders

    private val _riderActiveOrders = MutableLiveData<List<Order>>()
    val riderActiveOrders: LiveData<List<Order>> = _riderActiveOrders

    private val _riderStatusUpdated = MutableLiveData<Boolean>()
    val riderStatusUpdated: LiveData<Boolean> = _riderStatusUpdated

    private val _noNewOrder = MutableLiveData<Boolean>()
    val noNewOrder: LiveData<Boolean> = _noNewOrder

    private val _noActiveOrder = MutableLiveData<Boolean>()
    val noActiveOrder: LiveData<Boolean> = _noActiveOrder

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
                    _noNewOrder.value = orders.isEmpty()
                    fetchActiveOrders(riderId)
                },
                { error ->
                    _showLoading.value = false
                    _noNewOrder.value = true
//                    _responseMessage.value = "Please check your internet connection!"
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
                    _noActiveOrder.value = orders.isEmpty()
                },
                { error ->
                    _showLoading.value = false
                    _noActiveOrder.value = true
//                    _responseMessage.value = "Please check your internet connection!"
                }
            )
    }

    fun updateOrderStatus(order: Order) {
        _showLoading.value = true
        disposable = apiService.updateOrderStatus(
            orderId = order.OrderID,
            riderId = order.RiderID,
            flag = order.Status
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { success ->
                    _showLoading.value = false
                    fetchOrders(order.RiderID)
//                    if (!success.errorBody()?.string().isNullOrBlank())
//                        _responseMessage.value = success.errorBody()?.string()
                },
                { error ->
                    _showLoading.value = false
                    fetchOrders(order.RiderID)
//                    _responseMessage.value = "Please check your internet connection!"
                }
            )
    }

    fun saveToken(riderId: Long, token: String) {
        disposable = apiService.saveToken(riderId, token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({}, {})
    }

    fun updateStatus(riderId: Long, isActive: Boolean) {
        _showLoading.value = true
        disposable = apiService.updateStatus(riderId, isActive)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { result ->
                    _showLoading.value = false
                    _riderStatusUpdated.value = result.code() == 200
                    _responseMessage.value = result.errorBody()?.string()
                },
                { error ->
                    _showLoading.value = false
                    _riderStatusUpdated.value = false
//                    _responseMessage.value = "Please check your internet connection!"
                })
    }
}