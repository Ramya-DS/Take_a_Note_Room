package com.example.take_a_note_room.login.model

class LoginRepository(private val loginDao: LoginDao) {

    fun insert(loginEntity: LoginEntity) {
        loginDao.insert(loginEntity)
    }

    suspend fun update(loginEntity: LoginEntity) {
        loginDao.update(loginEntity)
    }

    suspend fun checkForUserName(userName: String): Int {
        return loginDao.checkForUserName(userName)
    }

    fun getUserId(userName: String): Int {
        return loginDao.getUserId(userName)
    }

    suspend fun getUsername(userId: Int): String {
        return loginDao.getUsername(userId)
    }

    suspend fun retrievePassword(userId: String): String {
        return loginDao.retrievePassword(userId)
    }
}