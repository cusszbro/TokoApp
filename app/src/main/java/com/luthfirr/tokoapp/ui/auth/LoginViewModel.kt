package com.luthfirr.tokoapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.data.repository.StoreRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val storeRepositoryImpl: StoreRepositoryImpl): ViewModel() {

    private val _loginResult = MutableSharedFlow<ApiResponse<Any>>()
    val loginResult: SharedFlow<ApiResponse<Any>> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginResult.emitAll(storeRepositoryImpl.getLogin(username, password))
        }
    }

}