package com.example.take_a_note_room.login.model

import androidx.lifecycle.LiveData

class LoginRepository(private val loginDao: LoginDao) {

    val allAccount: LiveData<List<LoginEntity>> = loginDao.getAllAccounts()

    suspend fun insert(loginEntity: LoginEntity): Boolean{
         try{
             loginDao.insert(loginEntity)
             return true
         }catch (e: Exception) {
             return false
         }
    }


    suspend fun checkForUserName(userId: String): Int {
        return loginDao.checkForUserName(userId)
    }


    suspend fun retrievePassword(userId: String): String {
        return loginDao.retrievePassword(userId)
    }
}