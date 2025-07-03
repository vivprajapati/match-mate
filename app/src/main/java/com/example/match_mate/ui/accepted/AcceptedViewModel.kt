package com.example.match_mate.ui.accepted

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.match_mate.data.model.User
import com.example.match_mate.data.repository.UserRepository
import kotlinx.coroutines.launch

class AcceptedViewModel(
) : ViewModel() {
    private var repository: UserRepository = UserRepository();
    val acceptedUsers: LiveData<List<User>> = repository.getAcceptedUsers()

}