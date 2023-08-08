package com.luthfirr.tokoapp.ui.store.detail

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.luthfirr.tokoapp.data.local.entity.Dashboard
import com.luthfirr.tokoapp.databinding.ItemDashboardStoreBinding

class StoreDetailViewHolder(private var binding: ItemDashboardStoreBinding): RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("Range")
    fun render(dashboard: Dashboard, listener: ((Dashboard) -> Unit)?) {
        binding.apply {

            itemStoreTvTitle.text = dashboard.title
            itemDashboardTvDate.text = dashboard.date
            itemDashboardTvPercentage.text = dashboard.percentage
            itemDashboardTvTargetResult.text = dashboard.target
            itemDashboardTvAchievementResult.text = dashboard.achievement
            itemDashboardCvItem.setCardBackgroundColor(Color.parseColor(dashboard.color))

            itemDashboardCvItem.setOnClickListener {
                listener?.invoke(dashboard)
            }
        }
    }
}