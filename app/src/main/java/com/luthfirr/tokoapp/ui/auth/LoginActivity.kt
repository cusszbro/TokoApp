package com.luthfirr.tokoapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.luthfirr.tokoapp.data.remote.network.ApiResponse
import com.luthfirr.tokoapp.databinding.ActivityLoginBinding
import com.luthfirr.tokoapp.ui.main.MainMenuActivity
import com.luthfirr.tokoapp.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private val viewModel : LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
        initObserver()
    }

    private fun initListener() {

        binding.apply {
            val usernameText = loginEtUsername.toString()
            val passwordText = loginEtPassword.toString()

            loginBtnLogin.setOnClickListener {
                when {
                    usernameText.isEmpty() && passwordText.isEmpty() -> {
                        loginIlUsername.error = "Kolom Username tidak boleh kosong"
                        loginIlPassword.error = "Kolom Username tidak boleh kosong"
                    }
                    usernameText.isEmpty() -> loginIlUsername.error = "Kolom Username tidak boleh kosong"
                    passwordText.isEmpty() -> loginIlPassword.error = "Kolom Username tidak boleh kosong"
                    else -> {
                        viewModel.login(
                            loginEtUsername.text.toString(),
                            loginEtPassword.text.toString()
                        )
                    }
                }
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginResult.collectLatest {

                binding.loginProgressBar.isVisible = it is ApiResponse.Loading

                if (it is ApiResponse.Error) {
                    makeToast(this@LoginActivity, it.errorMessage)
                }
                if (it is ApiResponse.Success) {
                    val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}