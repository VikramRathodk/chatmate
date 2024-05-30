package com.devvikram.chatmate.repository

import com.devvikram.chatmate.models.Users

interface UserRepository {
    suspend fun register(users: Users): Boolean
    suspend fun login(users: Users): Boolean
    fun setLoggedIn(isLoggedIn: Boolean)
    fun isLoggedIn(): Boolean
    fun setUserEmail(email: String)
    fun getUserEmail(): String?
    fun clear()
    fun logout()


}