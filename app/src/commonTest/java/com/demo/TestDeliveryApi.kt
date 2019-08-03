/*
package com.demo

import androidx.lifecycle.LiveData
import com.demo.repository.model.DeliveryData
import com.demo.repository.network.apiresponse.ApiResponse
import com.demo.repository.network.api.DeliveryApi
import com.google.android.gms.common.api.Api
import retrofit2.Call
import retrofit2.mock.Calls
import java.io.IOException
import javax.inject.Singleton


class TestDeliveryApi : DeliveryApi {

    private val dataModel = mutableListOf<DeliveryData>()
    var failureMsg: String? = null

    fun initiateDataModel(size: Int, description: String) {
        val deliveryDataFactory = DeliveryDataFactory()
        for (i in 0 until size) {
            dataModel.add(deliveryDataFactory.createDeliveryData(description))
        }
    }

    override fun getDeliveryData(offset: Int, limit: Int): LiveData<ApiResponse<List<DeliveryData>>> {

        failureMsg?.let {
            return Calls.failure(IOException(it))
        }
        initiateDataModel(limit, "Testing")
        val response = getDeliveryDataByRange(offset, limit)
        return Calls.response(response)
    }


    fun getDeliveryDataByRange(offset: Int, limit: Int): List<DeliveryData> {
        if (dataModel.size <= offset) {
            return emptyList()
        }
        return dataModel.subList(offset, Math.min(dataModel.size, offset + limit))
    }

    fun clear() {
        dataModel.clear()
    }

    fun resetFailureMsg() {
        failureMsg = null
    }


}
*/
