package com.example.take_a_note_room.login.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.take_a_note_room.login.model.LoginDatabase
import com.example.take_a_note_room.login.model.LoginEntity
import com.example.take_a_note_room.login.model.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: LoginRepository
    val allAccounts: LiveData<List<LoginEntity>>

    init {
        val loginDao = LoginDatabase.getDatabase(app, viewModelScope).loginDao()
        repository = LoginRepository(loginDao)
        allAccounts = repository.allAccount
    }

    fun insert(loginEntity: LoginEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(loginEntity)
    }

    suspend fun checkForUserName(userId: String): Boolean {
        val rowID= repository.checkForUserName(userId)
        if(rowID>0)
            return true
        return false
    }

    suspend fun authentication(userId: String, password: String): Boolean {
        return repository.retrievePassword(userId) == password

    }
}