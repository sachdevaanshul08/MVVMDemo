package com.demo.repositorytest.localtest.room


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.TestDeliveryApi
import com.demo.repository.db.dao.DeliveryDao
import com.demo.repository.db.database.DeliveryDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DeliveryTableTest {

    private lateinit var deliveryDao: DeliveryDao
    private lateinit var db: DeliveryDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DeliveryDatabase::class.java
        ).build()
        deliveryDao = db.getDeliveryDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeDeliveryTest() {
        val testApi = TestDeliveryApi()
        testApi.initiateDataModel(10, "Testing")
        val rawData = testApi.getDeliveryDataByRange(0, 10)
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