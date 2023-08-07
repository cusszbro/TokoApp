package com.luthfirr.tokoapp.data.remote

import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.data.remote.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

//    suspend fun getLogin(username: String, password: String) : Flow<ApiResponse<Any>> {
//
//    }
}