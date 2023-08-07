package com.luthfirr.tokoapp.di

import android.content.Context
import androidx.room.Room
import com.luthfirr.tokoapp.data.local.room.StoreDao
import com.luthfirr.tokoapp.data.local.room.StoreDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoreDatabase = Room.databaseBuilder(
        context,
        StoreDatabase::class.java, "Store.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideStoreDao(storeDatabase: StoreDatabase): StoreDao = storeDatabase.storeDao()
}