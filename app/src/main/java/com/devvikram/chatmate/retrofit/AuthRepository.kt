package com.devvikram.chatmate.retrofit

import SharedPreference
import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import com.devvikram.chatmate.MyApplication
import com.devvikram.chatmate.db.AppDatabase
import com.devvikram.chatmate.retrofit.model.LoginResponse
import com.devvikram.chatmate.retrofit.model.RegistrationResponse
import com.devvikram.chatmate.retrofit.model.Users
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


class AuthRepository(private val apiInterface: ApiInterface, activity: MyApplication ,private val appDatabase:AppDatabase) {
    private val sharedPreferences = SharedPreference(activity.applicationContext)

    suspend fun register(user: Users): RegistrationResponse {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "register: ${user.email},${user.password},${user.username}")
                val response = apiInterface.registerUser(user.email, user.password, user.username)
                Log.d(TAG, "register: --->  "+ response)

                if (response.isSuccessful) {
                    val registerResp = response.body()
                    if(registerResp?.status == true){
                        RegistrationResponse.Success(registerResp.message)
                    }else{
                        RegistrationResponse.Error(response.message())
                    }
                } else {
                    RegistrationResponse.Error("Something went wrong")
                }

            } catch (e: Exception) {
                RegistrationResponse.Error(e.message ?: "Something went wrong")
            }
        }
    }

    suspend fun login(user: Users, activity: Activity): LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiInterface.loginUser(user.email, user.password)

                if (response.isSuccessful) {
                    val loginRes = response.body()
                    Log.d(TAG, "login: response ${loginRes}")
                    if (loginRes != null) {
                        if (loginRes.status) {
                            Log.d(TAG, "login: res--->  ${loginRes.user}")
                            saveUserData(loginRes.user, activity)
                            LoginResponse.Success("Login Successful")
                        } else {
                            Log.d(TAG, "login: res--->  ${loginRes.message}")
                            LoginResponse.Error(loginRes.message ?: "Unknown error")
                        }
                    } else {
                        LoginResponse.Error("Null response body")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    LoginResponse.Error("HTTP error: $errorMessage")
                }
            } catch (e: IOException) {
                LoginResponse.Error("Network error: ${e.message ?: "Unknown error"}")
            } catch (e: JsonParseException) {
                LoginResponse.Error("JSON parsing error: ${e.message ?: "Unknown error"}")
            } catch (e: Exception) {
                e.printStackTrace()
                LoginResponse.Error("Something went wrong: ${e.message ?: "Unknown error"}")
            }
        }
    }



    private fun saveUserData(user: Users, activity: Activity) {
        Log.d(TAG, "saveUserData: " + user.email)
        Log.d(TAG, "saveUserData: ${user._id} ${user.email}+ ${user.username}+ ${user.password}")
        sharedPreferences.saveUserData(user)
    }

    suspend fun getUsers(activity: Activity): List<Users> {
        return withContext(Dispatchers.IO) {
            val cachedUsers = appDatabase.userDao().getAllUsers()

            if (cachedUsers.isNotEmpty()) {
                cachedUsers
            } else {
                try {
                    val response = apiInterface.getUsers().execute()
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        appDatabase.userDao().insert(users)
                        users
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }
        }
    }

}