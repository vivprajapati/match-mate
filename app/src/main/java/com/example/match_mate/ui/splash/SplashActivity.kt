package com.example.match_mate.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.match_mate.MainActivity
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private lateinit var viewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]

        binding.loadingAnimation.visibility = View.VISIBLE
        viewModel.loginStatusState.observe(this, Observer { result ->
            when (result) {
                is ApiResult.Success -> {
                    navigateToMainActivity()
                }

                is ApiResult.Error -> {
                    val errorMessage = result.message
                    showErrorUI(errorMessage)
                }

                ApiResult.Loading -> {
                    showLoadingUI()
                }
            }
        })
        viewModel.login()
    }

    private fun showLoadingUI() {
        binding.loadingAnimation.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
    }

    private fun showErrorUI(errorMessage: String?) {
        binding.loadingAnimation.visibility = View.GONE
        binding.errorText.text = errorMessage
        binding.errorText.visibility = View.VISIBLE
        binding.retryButton.visibility = View.VISIBLE

        binding.retryButton.setOnClickListener {
            binding.errorText.visibility = View.GONE
            binding.retryButton.visibility = View.GONE
            viewModel.login()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}