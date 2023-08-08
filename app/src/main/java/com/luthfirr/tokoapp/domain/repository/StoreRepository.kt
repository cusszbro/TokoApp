package com.luthfirr.tokoapp.domain.repository

import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import kotlinx.coroutines.flow.Flow

interface StoreRepository {

    fun getLogin(username: String, password: String) : Flow<ApiResponse<Any>>

    fun fetchStoreList(): Flow<ApiResponse<List<StoreEntity>>>

    fun fetchStore(roomId: Int): Flow<ApiResponse<StoreEntity>>
    fun updatePicture(roomId: Int, picture: String): Flow<ApiResponse<Any>>
    fun updateVisited(roomId: Int, visited: Boolean, lastVisited: Long): Flow<ApiResponse<Long>>

}