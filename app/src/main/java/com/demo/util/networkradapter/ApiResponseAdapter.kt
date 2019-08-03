package com.demo.util.networkradapter

import com.demo.data.network.apiresponse.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


/**
 * A Retrofit adapter that converts the Call into a NetworkListener of ApiResponse.
 * @param <R>
</R> */
class ApiResponseAdapter<R>(private val responseType: Type) :
    CallAdapter<R, NetworkListener<ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): NetworkListener<ApiResponse<R>> {
        return object : NetworkListener<ApiResponse<R>>() {
            override fun onCall(): NetworkListener<ApiResponse<R>> {
                call.enqueue(object : Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        set(ApiResponse.create(response))
                    }

                    override fun onFailure(call: Call<R>, throwable: Throwable) {
                        set(ApiResponse.create(throwable))
                    }
                })
                return this
            }
        }
    }


}


