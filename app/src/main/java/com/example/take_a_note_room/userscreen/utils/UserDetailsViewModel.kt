package com.example.take_a_note_room.userscreen.utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.take_a_note_room.database.TakeANote
import com.example.take_a_note_room.login.model.LoginRepository

class UserDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LoginRepository

    init {
        val loginDao = TakeANote.getDatabase(application, viewModelScope).loginDao()
        repository = LoginRepository(loginDao)
    }

    suspend fun getUsername(userId: Int): String {
        return repository.getUsername(userId)
    }
}