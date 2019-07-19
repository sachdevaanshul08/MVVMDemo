package com.demo.repository.network.user.api

import com.demo.repository.local.UserData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("deliveries")
    fun getUserData(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<List<UserData>>
}