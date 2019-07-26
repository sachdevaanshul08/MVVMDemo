package com.demo.repositorytest.localtest


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.FakeDeliveryApi
import com.demo.repository.local.database.DeliveryDatabase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RoomTest {

    val context = ApplicationProvider.getApplicationContext<Context>()
    private var db = Room.inMemoryDatabaseBuilder(
        context, DeliveryDatabase::class.java
    ).build()
    private var deliveryDao = db.getDeliveryDao()


    @Test
    @Throws(Exception::class)
    fun writeDeliveryTest() {
        val fakeDeliveryApi = FakeDeliveryApi()
        fakeDeliveryApi.initiateDataModel(10, "Testing")
        val rawData = fakeDeliveryApi.getDeliveryDataByRange(0, 10)
        deliveryDao.insert(rawData)
        val tableData = deliveryDao.getAllUser()
        assertEquals(rawData.size, tableData.size)
    }

    @Test
    @Throws(Exception::class)
    fun DeleteDeliveryTest() {
        deliveryDao.delete()
        val tableData = deliveryDao.getAllUser()
        assertEquals(tableData.size, 0)
    }
}