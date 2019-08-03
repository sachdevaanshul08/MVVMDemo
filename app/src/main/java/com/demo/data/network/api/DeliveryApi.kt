package com.demo.data.network.api

import com.demo.data.model.DeliveryData
import com.demo.data.network.apiresponse.ApiResponse
import com.demo.util.networkradapter.NetworkListener
import retrofit2.http.GET
import retrofit2.http.Query

interface DeliveryApi {
    @GET("deliveries")
    fun getDeliveryData(@Query("offset") offset: Int, @Query("limit") limit: Int): NetworkListener<ApiResponse<List<DeliveryData>>>

}