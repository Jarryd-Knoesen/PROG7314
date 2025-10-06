package com.example.prog7314_mobile_app_v2.Retrofit

import com.example.prog7314_mobile_app_v2.models.Users
import retrofit2.Call
import retrofit2.http.*

interface UsersApi {

    @GET("users")
    fun getAllUsers(): Call<List<Users>>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: String): Call<Users>

    @POST("users")
    fun createUser(@Body user: Users): Call<Users>

    @PUT("users/{id}")
    fun updateUser(@Path("id") id: String, @Body user: Users): Call<Void>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: String): Call<Void>
}
