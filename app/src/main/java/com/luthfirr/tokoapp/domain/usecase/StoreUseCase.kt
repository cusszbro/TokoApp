package com.luthfirr.tokoapp.domain.usecase

import com.luthfirr.tokoapp.data.Resource
import kotlinx.coroutines.flow.Flow

interface StoreUseCase {

    fun getLogin(username: String, password: String) : Flow<Resource<Any>>

}