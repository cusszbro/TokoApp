package com.luthfirr.tokoapp.ui.store.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.databinding.ItemStoreBinding

class StoreListAdapter: ListAdapter<StoreEntity, StoreListViewHolder>(StoreComparator) {

    var storeListener: ((StoreEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreListViewHolder {
        val binding = ItemStoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return StoreListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreListViewHolder, position: Int) {
        holder.render(getItem(position), storeListener)
    }

    object StoreComparator : DiffUtil.ItemCallback<StoreEntity>() {

        override fun areItemsTheSame(
            oldItem: StoreEntity,
            newItem: StoreEntity
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: StoreEntity,
            newItem: StoreEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

}