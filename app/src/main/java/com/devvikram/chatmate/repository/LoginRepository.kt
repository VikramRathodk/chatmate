package com.devvikram.chatmate.repository

import com.devvikram.chatmate.models.Users

abstract class LoginRepository {
    abstract suspend fun login(users: Users): Boolean
}