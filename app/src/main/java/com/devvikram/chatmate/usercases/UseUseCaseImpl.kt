package com.devvikram.chatmate.usercases

import com.devvikram.chatmate.models.Users
import com.devvikram.chatmate.repository.UserRepository

class UseUseCaseImpl(private val userRepository: UserRepository) : UserUseCase {
    override suspend fun register(users: Users): Boolean {
        if (users.email.isEmpty()) {
            return false
        }
        if (users.password.isEmpty()) {
            return false
        }
        return userRepository.register(users)
    }

    override suspend fun login(users: Users): Boolean {
        if (users.email.isEmpty()) {
            return false
        }
        if (users.password.isEmpty()) {
            return false
        }
        return userRepository.login(users)
    }

    override suspend fun logout() {
        userRepository.logout()
    }

    override fun isLoggedIn(): Boolean {
        return userRepository.isLoggedIn()
    }


}