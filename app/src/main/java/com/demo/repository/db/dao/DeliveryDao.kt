package com.demo.repository.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.repository.model.DeliveryData

@Dao
interface DeliveryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deliveryData: List<DeliveryData>)

    @Query("SELECT * FROM deliverydata")
    fun getAllUser(): List<DeliveryData>

    @Query("SELECT * FROM deliverydata LIMIT :limit OFFSET :offset")
    fun getUserDataChunk(offset: Int, limit: Int): LiveData<List<DeliveryData>>

    @Query("DELETE FROM deliverydata")
    fun delete()

    @Query("SELECT * FROM deliverydata ORDER BY indexInResponse ASC")
    fun getUser(): DataSource.Factory<Int, DeliveryData>
}