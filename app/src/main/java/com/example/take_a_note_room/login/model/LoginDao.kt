package com.example.take_a_note_room.login.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LoginDao {

    @Insert
    suspend fun insert(loginEntity: LoginEntity)

    @Query(value = "SELECT COUNT(*) from LoginDatabase where userName = :userId")
    suspend fun checkForUserName(userId: String): Int

    @Query(value = "SELECT password from LoginDatabase where userName = :userId")
    suspend fun retrievePassword(userId: String): String

    @Query("SELECT * from LoginDatabase")
    fun getAllAccounts(): LiveData<List<LoginEntity>>
}
