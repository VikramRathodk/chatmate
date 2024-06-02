package com.devvikram.chatmate.retrofit

import com.devvikram.chatmate.models.LoginResponse
import com.devvikram.chatmate.models.RegistrationResponse
import com.devvikram.chatmate.models.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository (private val apiInterface: ApiInterface){
    suspend fun register(user: Users): RegistrationResponse{
        return withContext(Dispatchers.IO){
            try {
                val response = apiInterface.registerUser(user.email, user.password, user.username)
                if(response.isSuccess){
                    RegistrationResponse.Success("Registration Successful")
                }else{
                    RegistrationResponse.Error("Something went wrong")
                }

            }catch (e: Exception){
                RegistrationResponse.Error(e.message ?: "Something went wrong")
            }
        }
    }
    suspend fun login(user: Users):LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiInterface.loginUser(user.email, user.password)
                if (response.isSuccess) {
                    LoginResponse.Success("Login Successful")
                } else {
                    LoginResponse.Error("Something went wrong")
                }
            } catch (e: Exception) {
                LoginResponse.Error(e.message ?: "Something went wrong")
            }
        }
    }
}