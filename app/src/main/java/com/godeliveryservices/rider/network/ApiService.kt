package com.godeliveryservices.rider.network

import com.godeliveryservices.rider.model.Order
import com.godeliveryservices.rider.model.Rider
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://web.godeliveryservice.com")
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

    @GET("/api/rider")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Observable<Response<Rider>>

    @GET("/api/order")
    fun fetchRiderOrders(
        @Query("RiderID") riderId: Long?,
        @Query("Status") status: String
    ): Observable<List<Order>>

    @POST("/api/order")
    fun updateOrderStatus(
        @Query("OrderID") orderId: Long,
        @Query("RiderID") riderId: Long?,
        @Query("Flag") flag: String
    ): Observable<Response<Void>>

    @POST("/api/rider")
    fun saveToken(
        @Query("RiderID") riderId: Long,
        @Query("Token") token: String
    ): Observable<Response<Void>>

    @GET("/api/order")
    fun getOrdersByRider(
        @Query("Status") status: String?,
        @Query("StartDate") startDate: String?,
        @Query("EndDate") endDate: String?,
        @Query("RiderID") riderId: Long? = null
    ): Observable<Response<List<Order>>>

    @POST("/api/location")
    fun updateLocation(
        @Query("RiderID") riderId: Long,
        @Query("location") location: String
    ): Observable<Response<Void>>

    @POST("/api/rider")
    fun updateStatus(
        @Query("RiderID") riderId: Long,
        @Query("Status") isActive: Boolean
    ): Observable<Response<Void>>
}