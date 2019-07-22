package com.demo.repository.network.user.api

import com.demo.repository.local.DeliveryData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeliveryApi {
    @GET("deliveries")
    fun getDeliveryData(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<List<DeliveryData>>
}