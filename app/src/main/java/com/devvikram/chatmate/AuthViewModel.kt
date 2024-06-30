package com.devvikram.chatmate

import SharedPreference
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devvikram.chatmate.models.LoginResponse
import com.devvikram.chatmate.models.RegistrationResponse
import com.devvikram.chatmate.models.Users
import com.devvikram.chatmate.retrofit.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val call = authRepository.getUsers(activity)
        call.enqueue(object : Callback<List<Users>>{
            override fun onResponse(call: Call<List<Users>>, response: Response<List<Users>>) {
                if (response.isSuccessful){
                    val userList  = response.body() ?: emptyList()
                    val filterUserList = userList.filter { it.email != SharedPreference(activity).getUserEmail() }
                    _userListLiveData.value = filterUserList
                }else{
                    _userListLiveData.value = emptyList()
                }
            }

            override fun onFailure(call: Call<List<Users>>, t: Throwable) {
                t.printStackTrace()
                println("Error: ${t.message}")
            }
        })
    }

}

