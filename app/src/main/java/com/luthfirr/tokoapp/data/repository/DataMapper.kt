package com.luthfirr.tokoapp.data.repository

import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.data.remote.response.Store

object DataMapper {
    fun mapResponseToEntity(input: List<Store?>?): List<StoreEntity> {
        val entityList = mutableListOf<StoreEntity>()

        if (input != null) {
            for (i in input) {
                val entity = StoreEntity(
                    storeId = i?.store_id,
                    storeCode = i?.store_code,
                    storeName = i?.store_name,
                    address = i?.address,
                    dcId = i?.dc_id,
                    dcName = i?.dc_name,
                    accountId = i?.account_id,
                    accountName = i?.account_name,
                    subchannelId = i?.subchannel_id,
                    subchannelName = i?.subchannel_name,
                    channelId = i?.channel_id,
                    channelName = i?.channel_name,
                    areaId = i?.area_id,
                    areaName = i?.area_name,
                    regionId = i?.region_id,
                    regionName = i?.region_name,
                    latitude = i?.latitude,
                    longitude = i?.longitude
                )
                entityList.add(entity)
            }
        }
        return entityList
    }
}