package com.devvikram.chatmate.repository

import com.devvikram.chatmate.models.Users
import com.devvikram.chatmate.usercases.LoginUserCase

class LoginUseCaseImpl(private val loginRepository: LoginRepository) : LoginUserCase {

    override suspend fun login(users: Users): Boolean {
        if (users.email.isEmpty()) {
            return false
        }
        if (users.password.isEmpty()) {
            return false
        }
        return loginRepository.login(users)
    }
}