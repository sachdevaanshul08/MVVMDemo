package com.demo.repository.network.api

import com.demo.repository.model.DeliveryData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeliveryApi {
    @GET("deliveries")
    fun getDeliveryData(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<List<DeliveryData>>
}