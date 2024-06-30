package com.devvikram.chatmate.retrofit

import com.devvikram.chatmate.models.LoginRes
import com.devvikram.chatmate.models.RegistrationRes
import com.devvikram.chatmate.models.Users
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("users/login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginRes>

    @FormUrlEncoded
    @POST("users/register")
    suspend fun registerUser(
        @Field(value = "email") email: String,
        @Field(value = "password") password: String,
        @Field(value = "username") name: String
    ): Response<RegistrationRes>

    //get users
    @GET("users/get_users")
    fun getUsers(): Call<List<Users>>

}