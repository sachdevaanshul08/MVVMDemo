package com.demo

import com.demo.repository.model.DeliveryData
import com.demo.repository.network.api.DeliveryApi
import retrofit2.Call
import retrofit2.mock.Calls
import java.io.IOException

class TestDeliveryApi : DeliveryApi {

    private val dataModel = mutableListOf<DeliveryData>()
    var failureMsg: String? = null

    fun initiateDataModel(size: Int, description: String) {
        var deliveryDataFactory = DeliveryDataFactory()
        for (i in 0 until size) {
            dataModel.add(deliveryDataFactory.createDeliveryData(description))
        }
    }

    override fun getDeliveryData(offset: Int, limit: Int): Call<List<DeliveryData>> {

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
