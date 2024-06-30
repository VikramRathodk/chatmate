package com.devvikram.chatmate.models

data class LoginRes(
    val status: Boolean,
    val message: String,
    val user: Users
)