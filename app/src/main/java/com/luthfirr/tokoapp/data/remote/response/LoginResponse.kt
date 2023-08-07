package com.luthfirr.tokoapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    val message: String? = null,
    val status: String? = null,
    val stores: List<Store?>? = null
): Parcelable