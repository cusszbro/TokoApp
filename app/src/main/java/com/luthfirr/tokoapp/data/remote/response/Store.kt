package com.luthfirr.tokoapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Store(
    val account_id: String? = null,
    val account_name: String? = null,
    val address: String? = null,
    val area_id: String? = null,
    val area_name: String? = null,
    val channel_id: String? = null,
    val channel_name: String? = null,
    val dc_id: String? = null,
    val dc_name: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val region_id: String? = null,
    val region_name: String? = null,
    val store_code: String? = null,
    val store_id: String? = null,
    val store_name: String? = null,
    val subchannel_id: String? = null,
    val subchannel_name: String? = null
): Parcelable