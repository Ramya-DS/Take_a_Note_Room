package com.example.take_a_note_room.login.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.take_a_note_room.database.TakeANote
import com.example.take_a_note_room.login.model.LoginEntity
import com.example.take_a_note_room.login.model.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: LoginRepository

    init {
        val loginDao = TakeANote.getDatabase(app, viewModelScope).loginDao()
        repository = LoginRepository(loginDao)
    }

    fun insert(loginEntity: LoginEntity) {
        repository.insert(loginEntity)
    }

    fun update(loginEntity: LoginEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(loginEntity)
    }

    fun getUserId(userName: String): Int {
        return repository.getUserId(userName)
    }

    suspend fun checkForUserName(userId: String): Boolean {
        val rowID = repository.checkForUserName(userId)
        if (rowID > 0)
            return true
        return false
    }

    suspend fun authentication(userId: String, password: String): Boolean {
        return repository.retrievePassword(userId) == password

    }
}