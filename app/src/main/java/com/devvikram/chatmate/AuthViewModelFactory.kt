package com.devvikram.chatmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.devvikram.chatmate.retrofit.AuthRepository

class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}