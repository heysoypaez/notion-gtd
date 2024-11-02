package com.example.notiongtd.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.notiongtd.data.model.OfflineData

@Dao
interface OfflineDataDao {
    @Insert
    suspend fun insert(data: OfflineData)

    @Query("SELECT * FROM offline_data")
    suspend fun getAll(): List<OfflineData>

    @Delete
    suspend fun delete(data: OfflineData)
}
