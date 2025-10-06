package com.example.prog7314_mobile_app_v2.Retrofit

import com.example.prog7314_mobile_app_v2.utils.FirestoreTimestampDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import java.util.*
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://trackstarapi-v1.onrender.com/api/"

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, FirestoreTimestampDeserializer())
        .create()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
