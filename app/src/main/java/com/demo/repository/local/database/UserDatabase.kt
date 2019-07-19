package com.demo.repository.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.repository.local.UserData
import com.demo.repository.local.dao.UserDataDao
import com.demo.util.LocationTypeConverter

@Database(entities = [UserData::class], version = 3)
@TypeConverters(LocationTypeConverter::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao
}