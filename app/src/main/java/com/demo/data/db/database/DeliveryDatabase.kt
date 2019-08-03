package com.demo.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.BuildConfig
import com.demo.data.model.DeliveryData
import com.demo.data.db.dao.DeliveryDao
import com.demo.util.LocationTypeConverter

@Database(entities = [DeliveryData::class], version = BuildConfig.DATABASE_VERSION, exportSchema = false)
@TypeConverters(LocationTypeConverter::class)
abstract class DeliveryDatabase : RoomDatabase() {
    abstract fun getDeliveryDao(): DeliveryDao
}