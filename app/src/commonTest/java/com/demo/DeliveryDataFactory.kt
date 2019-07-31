package com.demo

import com.demo.repository.model.DeliveryData
import com.demo.repository.model.LocationData
import java.util.concurrent.atomic.AtomicInteger


class DeliveryDataFactory {

    private val counter = AtomicInteger(0)
    fun createDeliveryData(data: String): DeliveryData {
        val id = counter.incrementAndGet()
        return DeliveryData(
            id = id, description = data + " $id",
            imageUrl = "https://s3-ap-southeast-1.amazonaws.com/lalamove-mock-api/images/pet-1.jpeg",
            location = LocationData(id.toDouble(), id.toDouble(), "address $id"),
            indexInResponse = id
        )
    }
}
