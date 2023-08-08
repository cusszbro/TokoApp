package com.luthfirr.tokoapp.data.repository

import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.data.local.room.StoreDao
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.data.remote.network.ApiService
import com.luthfirr.tokoapp.data.remote.response.LoginResponse
import com.luthfirr.tokoapp.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val storeDao: StoreDao
) : StoreRepository {

    override fun getLogin(username: String, password: String): Flow<ApiResponse<Any>> = flow {
        try {
            emit(ApiResponse.Loading)
            val result = apiService.login(username, password)
            if (result.isSuccessful) {
                val loginResponse = result.body() as LoginResponse
                if (loginResponse.stores != null) {
                    val entityList = DataMapper.mapResponseToEntity(loginResponse.stores)
                    storeDao.deleteAllStore()
                    storeDao.insertStoreList(entityList)
                    emit(ApiResponse.Success(Any()))
                } else emit(ApiResponse.Error(loginResponse.message.toString()))
            } else {
                val jsonObj = JSONObject(result.errorBody()!!.charStream().readText())
                val message = jsonObj.getString("message")
                emit(ApiResponse.Error(message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Error(e.localizedMessage!!))
        }
    }

    override fun fetchStoreList(): Flow<ApiResponse<List<StoreEntity>>> = flow {
        try {
            emit(ApiResponse.Loading)
            val storeList = storeDao.fetchAllStore()
            emit(ApiResponse.Success(storeList))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Error(e.localizedMessage!!))
        }
    }

    override fun fetchStore(roomId: Int): Flow<ApiResponse<StoreEntity>> = flow {
        try {
            emit(ApiResponse.Loading)
            val entity = storeDao.fetchStore(roomId)
            emit(ApiResponse.Success(entity))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Error(e.localizedMessage!!))
        }
    }

    override fun updatePicture(roomId: Int, picture: String): Flow<ApiResponse<Any>> = flow {
        try {
            emit(ApiResponse.Loading)
            storeDao.updatePicture(roomId, picture)
            emit(ApiResponse.Success(Any()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Error(e.localizedMessage!!))
        }
    }

    override fun updateVisited(
        roomId: Int,
        visited: Boolean,
        lastVisited: Long
    ): Flow<ApiResponse<Long>> = flow {
        try {
            emit(ApiResponse.Loading)
            storeDao.updateVisited(roomId, visited, lastVisited)
            emit(ApiResponse.Success(lastVisited))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Error(e.localizedMessage!!))
        }
    }

}