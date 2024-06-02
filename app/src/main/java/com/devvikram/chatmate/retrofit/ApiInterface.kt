package com.devvikram.chatmate.retrofit

import com.android.volley.Response
import com.devvikram.chatmate.models.LoginResponse
import com.devvikram.chatmate.models.RegistrationResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field(value = "email") email: String,
        @Field(value = "password") password: String,
        @Field(value = "username") name: String
    ): Response<RegistrationResponse>

}