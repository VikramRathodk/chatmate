package com.devvikram.chatmate.usercases

import com.devvikram.chatmate.models.Users

interface UserUseCase {
    suspend fun register(users: Users):Boolean
    suspend fun login(users: Users):Boolean
    suspend fun logout()
     fun isLoggedIn():Boolean
}