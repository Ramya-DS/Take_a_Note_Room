package com.example.take_a_note_room.login.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LoginDao {

    @Insert
    fun insert(loginEntity: LoginEntity)

    @Query(value = "SELECT COUNT(*) from LoginDatabase where userName = :userName")
    suspend fun checkForUserName(userName: String): Int

    @Query(value = "SELECT password from LoginDatabase where userName = :userName")
    suspend fun retrievePassword(userName: String): String

    @Query("SELECT userId from LoginDatabase where userName= :userName")
    fun getUserId(userName: String): Int

    @Query("SELECT userName from LoginDatabase where userId= :userId")
    suspend fun getUsername(userId: Int): String

    @Update
    suspend fun update(loginEntity: LoginEntity)
}
