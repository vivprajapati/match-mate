package com.example.match_mate.ui.home

import androidx.lifecycle.*
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.data.model.User
import com.example.match_mate.data.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private var repository: UserRepository = UserRepository()
    private var currentPage = 1
    private val pageSize = 20

    private val _acceptedUsersState = MutableLiveData<ApiResult<User>>(ApiResult.Loading)
    val acceptedUsersState: LiveData<ApiResult<User>> = _acceptedUsersState
    private val _declinedUsersState = MutableLiveData<ApiResult<User>>(ApiResult.Loading)
    val declinedUsersState: LiveData<ApiResult<User>> = _declinedUsersState
    private var isDbExhausted = false

    private val _users = MutableLiveData<ApiResult<List<User>>>()
    val users: LiveData<ApiResult<List<User>>> = _users

    fun loadNextPage() {
        viewModelScope.launch {
            if (!isDbExhausted) {
                val dbResult = repository.getUsersPaginated(currentPage, pageSize)
                if (dbResult is ApiResult.Success && dbResult.data.isNotEmpty()) {
                    _users.value = dbResult
                    currentPage++
                } else {
                    isDbExhausted = true
                    fetchAndStoreFromServer()
                }
            } else {
                fetchAndStoreFromServer()
            }
        }
    }

    private suspend fun fetchAndStoreFromServer() {
        val apiResult = repository.fetchUsers()
        if (apiResult is ApiResult.Success && apiResult.data.isNotEmpty()) {
            isDbExhausted = false
            val dbResult = repository.getUsersPaginated(currentPage, pageSize)
            if (dbResult is ApiResult.Success && dbResult.data.isNotEmpty()) {
                _users.value = dbResult
                currentPage++
            } else {
                _users.value = ApiResult.Success(emptyList())
            }
        } else {
            _users.value = apiResult
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
                _declinedUsersState.value = ApiResult.Error(e.hashCode(), "Failed to decline user: ${e.message}")
            }
        }
    }
}
