package com.heysoypaez.notiongtd.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.heysoypaez.notiongtd.data.model.OfflineData

@Database(entities = [OfflineData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun offlineDataDao(): OfflineDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
