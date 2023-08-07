package com.luthfirr.tokoapp.ui.store.list

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.luthfirr.tokoapp.R
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.databinding.ItemStoreBinding

class StoreListViewHolder(private var binding: ItemStoreBinding): RecyclerView.ViewHolder(binding.root) {

    fun render(storeEntity: StoreEntity, listener: ((StoreEntity) -> Unit)?) {
        binding.apply {
            itemStoreStoreName.text = storeEntity.storeName
            itemStoreStoreCluster.text = storeEntity.address
            itemStoreStoreTtRegular.text = itemView.resources.getString(
                R.string.store_area_region,
                storeEntity.areaName,
                storeEntity.regionName
            )
            itemStoreIvChecked.isVisible = storeEntity.visited != null && storeEntity.visited
            itemStoreStoreTvDistance.text = storeEntity.distance

            itemStoreCvStore.setOnClickListener {
                listener?.invoke(storeEntity)
            }
        }
    }
}