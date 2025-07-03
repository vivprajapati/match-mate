package com.example.match_mate.ui.declined


import androidx.lifecycle.*
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.data.model.User
import com.example.match_mate.data.repository.UserRepository
import kotlinx.coroutines.launch

class DeclinedViewModel(
) : ViewModel() {
    private var repository: UserRepository = UserRepository();

    private val _allUsersState = MutableLiveData<ApiResult<List<User>>>(ApiResult.Loading)
    val allUsersState: LiveData<ApiResult<List<User>>> = _allUsersState

    fun fetchAcceptedUsers() {
        viewModelScope.launch {
            _allUsersState.value = ApiResult.Loading
            val result = repository.getDeclinedUsers()
            result.let { apiResult ->
                when (apiResult) {
                    is ApiResult.Success -> {
                        _allUsersState.value = ApiResult.Success(apiResult.data)
                    }
                    is ApiResult.Error -> {
                        _allUsersState.value = ApiResult.Error(apiResult.code, apiResult.message)
                    }
                    ApiResult.Loading -> {
                        // Keep the state as Loading
                    }
                }
            }
        }
    }

}