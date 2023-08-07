package com.luthfirr.tokoapp.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luthfirr.tokoapp.data.local.entity.StoreEntity

@Database(
    entities = [StoreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoreDatabase : RoomDatabase() {

    abstract fun storeDao(): StoreDao
}