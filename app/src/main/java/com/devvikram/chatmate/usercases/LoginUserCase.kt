package com.devvikram.chatmate.usercases

import com.devvikram.chatmate.models.Users

interface LoginUserCase {
    suspend fun login(users: Users):Boolean
    suspend fun logout()
     fun isLoggedIn():Boolean
}