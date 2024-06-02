package com.devvikram.chatmate

import android.app.Application
import com.devvikram.chatmate.retrofit.AuthRepository
import com.devvikram.chatmate.retrofit.RetrofitInstance


class MyApplication : Application() {

    lateinit var  authRepository: AuthRepository
    override fun onCreate() {
        super.onCreate()
        var apiService = RetrofitInstance.getApiService()
        authRepository = AuthRepository(apiService)
    }



}