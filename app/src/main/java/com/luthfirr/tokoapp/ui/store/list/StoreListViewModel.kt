package com.luthfirr.tokoapp.ui.store.list

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
class StoreListViewModel @Inject constructor(private val storeRepositoryImpl: StoreRepositoryImpl):  ViewModel() {
    private val _storeListResult = MutableSharedFlow<ApiResponse<List<StoreEntity>>>()
    val storeListResult: SharedFlow<ApiResponse<List<StoreEntity>>> = _storeListResult

    fun fetchStoreList() {
        viewModelScope.launch {
            _storeListResult.emitAll(storeRepositoryImpl.fetchStoreList())
        }
    }
}