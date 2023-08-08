package com.luthfirr.tokoapp.ui.store.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.luthfirr.tokoapp.data.local.entity.Dashboard
import com.luthfirr.tokoapp.databinding.ItemDashboardStoreBinding

class StoreDetailAdapter: ListAdapter<Dashboard, StoreDetailViewHolder>(StoreComparator) {

    var storeListener: ((Dashboard) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreDetailViewHolder {
        val binding = ItemDashboardStoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return StoreDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreDetailViewHolder, position: Int) {
        holder.render(getItem(position), storeListener)
    }

    object StoreComparator : DiffUtil.ItemCallback<Dashboard>() {

        override fun areItemsTheSame(
            oldItem: Dashboard,
            newItem: Dashboard
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Dashboard,
            newItem: Dashboard
        ): Boolean {
            return oldItem == newItem
        }
    }

}