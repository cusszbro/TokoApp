package com.luthfirr.tokoapp.ui.store.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luthfirr.tokoapp.data.local.entity.StoreEntity
import com.luthfirr.tokoapp.databinding.ActivityDetailStoreBinding
import com.luthfirr.tokoapp.ui.main.MainMenuActivity
import com.luthfirr.tokoapp.utils.makeToast

class StoreDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storeData = intent.getParcelableExtra<StoreEntity>(STORE)

        initView(storeData)
        initListener()
    }

    private fun initView(storeEntity: StoreEntity?) {
        binding.apply {
            storeDetailTvCode.text = storeEntity?.storeCode
            storeDetailTvName.text = storeEntity?.storeName
            storeDetailTvCluster.text = storeEntity?.address
            storeDetailTvTtRegular.text = storeEntity?.regionName
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
        }
    }

    companion object {
        const val STORE = "store"
        const val PHOTO = "photo"
    }
}