package com.example.match_mate.ui.declined


import androidx.lifecycle.*
import com.example.match_mate.data.model.User
import com.example.match_mate.data.repository.UserRepository
import kotlinx.coroutines.launch

class DeclinedViewModel(
) : ViewModel() {
    private var repository: UserRepository = UserRepository();

    val declinedUsers: LiveData<List<User>> = repository.getDeclinedUsers()

}