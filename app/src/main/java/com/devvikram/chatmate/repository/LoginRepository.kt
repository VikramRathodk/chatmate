package com.devvikram.chatmate.repository

import com.devvikram.chatmate.models.Users

interface LoginRepository {
    suspend fun login(users: Users): Boolean
    fun setLoggedIn(isLoggedIn: Boolean)
    fun isLoggedIn(): Boolean
    fun setUserEmail(email: String)
    fun getUserEmail(): String?
    fun clear()
    fun logout()


}