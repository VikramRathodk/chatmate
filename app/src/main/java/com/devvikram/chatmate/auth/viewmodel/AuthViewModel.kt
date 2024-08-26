package com.devvikram.chatmate.auth.viewmodel

import SharedPreference
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.chatmate.retrofit.AuthRepository
import com.devvikram.chatmate.retrofit.model.LoginResponse
import com.devvikram.chatmate.retrofit.model.RegistrationResponse
import com.devvikram.chatmate.retrofit.model.Users
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registrationState = MutableLiveData<RegistrationResponse>()
    fun registerUser(user: Users) = viewModelScope.launch {
        _registrationState.value = authRepository.register(user)
    }

    val registrationState: LiveData<RegistrationResponse> get() = _registrationState
    private val _loginState = MutableLiveData<LoginResponse>()
    val loginState: LiveData<LoginResponse> get() = _loginState

    fun loginUser(user: Users, activity: Activity) = viewModelScope.launch {
        _loginState.value = authRepository.login(user, activity)
    }


    fun logoutUser(activity: Activity, intent: Intent) = viewModelScope.launch {
        val sharedPreference = SharedPreference(activity)
        sharedPreference.clearUserData()
        activity.startActivity(intent)
        activity.finish()
    }

    fun isLoggedIn(activity: Activity): Boolean {
        val sharedPreference = SharedPreference(activity)
        return sharedPreference.isLoggedIn()
    }

    private val _userListLiveData = MutableLiveData<List<Users>>()
    val userListLiveData: LiveData<List<Users>> get() = _userListLiveData
    fun getAllUsers(activity: Activity) = viewModelScope.launch {
        val users = authRepository.getUsers(activity)
        val filteredUsers = users.filter { it.email != SharedPreference(activity).getUserEmail() }
        _userListLiveData.value = filteredUsers
    }


}

