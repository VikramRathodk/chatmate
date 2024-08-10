package com.devvikram.chatmate.retrofit.model

import com.devvikram.chatmate.retrofit.model.Users

data class LoginRes(
    val status: Boolean,
    val message: String,
    val user: Users
)