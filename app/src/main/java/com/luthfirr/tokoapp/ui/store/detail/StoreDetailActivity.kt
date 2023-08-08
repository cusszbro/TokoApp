package com.luthfirr.tokoapp.ui.store.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luthfirr.tokoapp.data.local.entity.Dashboard
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.databinding.ActivityDetailStoreBinding
import com.luthfirr.tokoapp.ui.main.MainMenuActivity
import com.luthfirr.tokoapp.ui.store.visit.StoreVisitActivity
import com.luthfirr.tokoapp.utils.makeToast

class StoreDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoreBinding
    private val storeDetailAdapter = StoreDetailAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeData = intent.getParcelableExtra<StoreEntity>(STORE)

        initView(storeData)
        initListener()
    }

    private fun initView(storeEntity: StoreEntity?) {
        val dataDummy = getDummyDataDashboardList()
        storeDetailAdapter.submitList(dataDummy)

        binding.apply {
            detailStoreLlNotif.isSelected = true
            storeDetailTvCode.text = storeEntity?.storeCode
            storeDetailTvName.text = storeEntity?.storeName
            storeDetailTvCluster.text = storeEntity?.address
            storeDetailTvTtRegular.text = storeEntity?.regionName

            dashboardDetailLayout.dashboardRv.adapter = storeDetailAdapter

        }
    }
    private fun initListener() {
        binding.apply {
            detailStoreBtnFinish.setOnClickListener {
                val intent = Intent(this@StoreDetailActivity, MainMenuActivity::class.java)
                startActivity(intent)
                finish()
            }

            menuDetailLayout.menuDetailLlInfo.setOnClickListener {
                makeToast(this@StoreDetailActivity, "Kamu memilih Menu Information")
            }

            menuDetailLayout.menuDetailLlProduct.setOnClickListener {
                makeToast(this@StoreDetailActivity, "Kamu memilih Menu Product Check")
            }

            menuDetailLayout.menuDetailLlSku.setOnClickListener {
                makeToast(this@StoreDetailActivity, "Kamu memilih Menu SKU Promo")
            }

            menuDetailLayout.menuDetailLlSummary.setOnClickListener {
                makeToast(this@StoreDetailActivity, "Kamu memilih Menu Ringkasan OOS")
            }

            menuDetailLayout.menuDetailLlInvestment.setOnClickListener {
                makeToast(this@StoreDetailActivity, "Kamu memilih Menu Store Investment")
            }

            storeDetailAdapter.storeListener = {
                makeToast(this@StoreDetailActivity, it.title)
            }
        }
    }

    private fun getDummyDataDashboardList(): List<Dashboard> {
        return listOf(
            Dashboard(
                title = "OSA",
                date = "September 2020",
                percentage = "50%",
                target = "40",
                achievement = "20",
                color = "#f1b117",
            ),
            Dashboard(
                title = "NPD",
                date = "September 2020",
                percentage = "80%",
                target = "100",
                achievement = "80",
                color = "#1dcabd",
            ),
            Dashboard(
                title = "XYZ",
                date = "September 2020",
                percentage = "100%",
                target = "100",
                achievement = "10",
                color = "#3473b2",
            )
        )
    }

    companion object {
        const val STORE = "store"
        const val PHOTO = "photo"
    }
}