package com.demo.repository.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.repository.local.DeliveryData

@Dao
interface DeliveryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deliveryData: List<DeliveryData>)

    @Query("SELECT * FROM userdata LIMIT :limit OFFSET :offset")
    fun getUserDataChunk(offset: Int, limit: Int): LiveData<List<DeliveryData>>

    @Query("DELETE FROM userdata")
    fun delete()

    @Query("SELECT * FROM userdata ORDER BY indexInResponse ASC")
    fun getUser(): DataSource.Factory<Int, DeliveryData>
}