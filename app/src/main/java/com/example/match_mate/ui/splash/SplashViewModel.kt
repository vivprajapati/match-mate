package com.example.match_mate.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.data.model.User
import com.example.match_mate.data.repository.UserRepository
import com.example.match_mate.utils.AppSession
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _loginStatusState = MutableLiveData<ApiResult<User>>(ApiResult.Loading)
    val loginStatusState: LiveData<ApiResult<User>> = _loginStatusState

    private val userRepository = UserRepository()

    fun login() {
        viewModelScope.launch {
            _loginStatusState.value = ApiResult.Loading

            try {
                val user: User? = userRepository.getLoggedInUser()
                if (user != null) {
                    AppSession.currentUser = user
                    _loginStatusState.value = ApiResult.Success(user)
                } else {
                    val result = userRepository.fetchLoggedInUser()
                    result.let { apiResult ->
                        when (apiResult) {
                            is ApiResult.Success -> {
                                AppSession.currentUser = apiResult.data
                                _loginStatusState.value = ApiResult.Success(apiResult.data)
                            }

                            is ApiResult.Error -> {
                                _loginStatusState.value = ApiResult.Error(apiResult.code, apiResult.message)
                            }

                            ApiResult.Loading -> {

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _loginStatusState.value = ApiResult.Error(e.hashCode(), "Failed to check login status: ${e.message}")
                Log.e("SplashActivity", "Error: ${e.message}")
            }
        }
    }

}