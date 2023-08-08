package com.luthfirr.tokoapp.ui.store.visit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.data.repository.StoreRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreVisitViewModel @Inject constructor(private val storeRepositoryImpl: StoreRepositoryImpl): ViewModel() {
    private val _storeResult = MutableSharedFlow<ApiResponse<StoreEntity>>()
    val storeResult: SharedFlow<ApiResponse<StoreEntity>> = _storeResult

    private val _storePictureResult = MutableSharedFlow<ApiResponse<Any>>()
    val storePictureResult: SharedFlow<ApiResponse<Any>> = _storePictureResult

    private val _storeVisitedResult = MutableSharedFlow<ApiResponse<Long>>()
    val storeVisitedResult: SharedFlow<ApiResponse<Long>> = _storeVisitedResult

    fun fetchStore(roomId: Int) {
        viewModelScope.launch {
            _storeResult.emitAll(storeRepositoryImpl.fetchStore(roomId))
        }
    }

    fun updatePicture(roomId: Int, picture: String) {
        viewModelScope.launch {
            _storePictureResult.emitAll(storeRepositoryImpl.updatePicture(roomId, picture))
        }
    }

    fun updateVisited(roomId: Int, visited: Boolean, lastVisited: Long) {
        viewModelScope.launch {
            _storeVisitedResult.emitAll(
                storeRepositoryImpl.updateVisited(
                    roomId,
                    visited,
                    lastVisited
                )
            )
        }
    }
}