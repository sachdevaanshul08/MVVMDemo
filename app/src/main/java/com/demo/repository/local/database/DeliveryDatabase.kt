package com.demo.repository.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.repository.local.DeliveryData
import com.demo.repository.local.dao.DeliveryDao
import com.demo.util.LocationTypeConverter

@Database(entities = [DeliveryData::class], version = 3)
@TypeConverters(LocationTypeConverter::class)
abstract class DeliveryDatabase : RoomDatabase() {
    abstract fun getDeliveryDao(): DeliveryDao
}