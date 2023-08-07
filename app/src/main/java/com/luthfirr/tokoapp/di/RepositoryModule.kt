package com.luthfirr.tokoapp.di

import com.luthfirr.tokoapp.data.repository.StoreRepositoryImpl
import com.luthfirr.tokoapp.domain.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideRepository(storeRepositoryImpl: StoreRepositoryImpl): StoreRepository
}