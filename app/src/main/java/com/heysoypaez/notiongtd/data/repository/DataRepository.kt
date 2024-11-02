package com.heysoypaez.notiongtd.data.repository

import android.content.Context
import com.heysoypaez.notiongtd.data.local.database.AppDatabase
import com.heysoypaez.notiongtd.data.model.OfflineData
import com.heysoypaez.notiongtd.utils.isInternetAvailable
import kotlinx.coroutines.delay

typealias Handler = () -> Unit

class DataRepository(private val context: Context, private val database: AppDatabase) {

    private fun getOfflineData(data: String): OfflineData {
        return OfflineData(data = data)
    }

    private suspend fun saveDataOffline(data: String, offlineHandler: Handler) {
        println("NO Internet, storing locally")
        database.offlineDataDao().insert(this.getOfflineData(data))
        offlineHandler()
    }

    private suspend fun saveDataOnline(
        data: String,
        onlineHandler: Handler,
        offlineHandler: Handler
    ) {
        try {
            println("Internet OK, sending data")
            onlineHandler()
        } catch (e: Exception) {
            this.saveDataOffline(data, offlineHandler)
        }
    }

    suspend fun saveData(data: String, onlineHandler: Handler, offlineHandler: Handler) {
        if (!isInternetAvailable(context)) {
            return this.saveDataOffline(data, offlineHandler)
        }
        this.saveDataOnline(data, onlineHandler, offlineHandler)
    }

    suspend fun syncData(
        updateCallback: (item: String) -> Unit,
        onSuccess: (itemsUpdated: Int) -> Unit,
        onOffline: (itemsUpdated: Int) -> Unit

    ) {
        val dataToSync = database.offlineDataDao().getAll()
        val itemsSynced = dataToSync.size


        if (!isInternetAvailable(context)) return onOffline(itemsSynced)

        dataToSync.forEach { data ->
            try {
                delay(500)
                updateCallback(data.data)
                database.offlineDataDao().delete(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onSuccess(itemsSynced)
    }
}
