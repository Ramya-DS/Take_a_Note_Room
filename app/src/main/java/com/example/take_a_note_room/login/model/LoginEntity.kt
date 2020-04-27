package com.example.take_a_note_room.login.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LoginDatabase")
data class LoginEntity(@PrimaryKey val userName: String, val password: String) {
    override fun toString(): String {
        return "User ID: $userName\tPassword: $password"
    }
}