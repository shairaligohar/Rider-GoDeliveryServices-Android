package com.godeliveryservices.rider.ui.cash_reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CashReportsViewModel : ViewModel() {
    private val apiService = ApiService.create()
    private var disposable: Disposable? = null

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    val orderFilters = MutableLiveData<OrderFilters>()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    fun fetchOrders(riderId: Long) {
        _showLoading.value = true
        val filters = orderFilters.value
        disposable = apiService.getOrdersByRider(
            status = "Delivered",
            startDate = filters?.startDate,
            endDate = filters?.endDate,
            riderId = riderId
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ success ->
                _showLoading.value = false
                if (success.code() == 200) {
                    _orders.value = success.body()
                } else {
                    _responseMessage.value = success.message()
                }
            }, { error ->
                _showLoading.value = false
                _responseMessage.value = error.message
            })
    }
}