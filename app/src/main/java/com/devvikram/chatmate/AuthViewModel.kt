package com.devvikram.chatmate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.chatmate.models.LoginResponse
import com.devvikram.chatmate.models.RegistrationResponse
import com.devvikram.chatmate.models.Users
import com.devvikram.chatmate.retrofit.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registrationState = MutableLiveData<RegistrationResponse>()
    val registrationState: LiveData<RegistrationResponse> get() = _registrationState

    private val _loginState = MutableLiveData<LoginResponse>()
    val loginState: LiveData<LoginResponse> get() = _loginState


    fun registerUser(user: Users) = viewModelScope.launch {
        _registrationState.value = authRepository.register(user)
    }

    fun loginUser(user: Users) = viewModelScope.launch {
        _loginState.value = authRepository.login(user)
    }
}