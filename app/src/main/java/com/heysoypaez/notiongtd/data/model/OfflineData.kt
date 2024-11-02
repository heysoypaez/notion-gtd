package com.heysoypaez.notiongtd.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_data")
data class OfflineData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val data: String
)
