package com.devvikram.chatmate.usercases

import com.devvikram.chatmate.models.Users
import com.devvikram.chatmate.repository.LoginRepository

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

    override suspend fun logout() {
        loginRepository.logout()
    }

    override fun isLoggedIn(): Boolean {
        return loginRepository.isLoggedIn()
    }


}