package com.example.match_mate.ui.home

import androidx.lifecycle.*
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.data.model.User
import com.example.match_mate.data.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private var repository: UserRepository = UserRepository()

    private val _allUsersState = MutableLiveData<ApiResult<List<User>>>(ApiResult.Loading)
    val allUsersState: LiveData<ApiResult<List<User>>> = _allUsersState

    private val _acceptedUsersState = MutableLiveData<ApiResult<User>>(ApiResult.Loading)
    val acceptedUsersState: LiveData<ApiResult<User>> = _acceptedUsersState
    private val _declinedUsersState = MutableLiveData<ApiResult<User>>(ApiResult.Loading)
    val declinedUsersState: LiveData<ApiResult<User>> = _declinedUsersState

    fun loadUsers() {
        viewModelScope.launch {
            _allUsersState.value = ApiResult.Loading
            val result = repository.fetchUsers()
            result.let { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        _allUsersState.value = ApiResult.Success(apiResult.data)
                    }

                    is ApiResult.Error -> {
                        _allUsersState.value = ApiResult.Error(apiResult.code, apiResult.message)
                    }

                    ApiResult.Loading -> {
                    }
                }
            }

        }
    }

    fun acceptUser(user: User) {
        viewModelScope.launch {
            try {
               val result = repository.updateUserStatus(user, "accepted")
                result.let {
                    when (it) {
                        is ApiResult.Success -> {
                            _acceptedUsersState.value = ApiResult.Success(it.data)
                        }
                        is ApiResult.Error -> {
                            _acceptedUsersState.value = ApiResult.Error(it.code, it.message)
                        }
                        ApiResult.Loading -> {
                            // Keep the state as Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _acceptedUsersState.value = ApiResult.Error(e.hashCode(), "Failed to accept user: ${e.message}")
            }
        }
    }

    fun declineUser(user: User) {
        viewModelScope.launch {
            try {
                val result = repository.updateUserStatus(user, "declined")
                result.let {
                    when (it) {
                        is ApiResult.Success -> {
                            _declinedUsersState.value = ApiResult.Success(it.data)
                        }
                        is ApiResult.Error -> {
                            _declinedUsersState.value = ApiResult.Error(it.code, it.message)
                        }
                        ApiResult.Loading -> {
                        }
                    }
                }
            } catch (e: Exception) {
                _allUsersState.value = ApiResult.Error(e.hashCode(), "Failed to decline user: ${e.message}")
            }
        }
    }
}
