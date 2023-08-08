package com.luthfirr.tokoapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.luthfirr.tokoapp.databinding.ActivityMainMenuBinding
import com.luthfirr.tokoapp.ui.auth.LoginActivity
import com.luthfirr.tokoapp.ui.store.list.StoreListActivity
import com.luthfirr.tokoapp.utils.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
        binding.apply {
            mainMenuBtnRefresh.setOnClickListener {
                mainMenuProgressBar.isVisible = true
                CoroutineScope(Dispatchers.Main).launch {
                    onRestart()
                    delay(2000)
                    mainMenuProgressBar.isVisible = false
                }
            }

            menuLayout.mainMenuLlKunjungan.setOnClickListener {
                startActivity(Intent(this@MainMenuActivity, StoreListActivity::class.java))
            }

            menuLayout.mainMenuLlTarget.setOnClickListener {
                makeToast(this@MainMenuActivity, "Kamu memilih Menu Target Install CDFDPC")
            }

            menuLayout.mainMenuLlDashboard.setOnClickListener {
                makeToast(this@MainMenuActivity, "Kamu memilih Menu Dashboard")
            }

            menuLayout.mainMenuLlHistory.setOnClickListener {
                makeToast(this@MainMenuActivity, "Kamu memilih Menu Transmission History")
            }

            menuLayout.mainMenuLlLogout.setOnClickListener {
                startActivity(Intent(this@MainMenuActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}