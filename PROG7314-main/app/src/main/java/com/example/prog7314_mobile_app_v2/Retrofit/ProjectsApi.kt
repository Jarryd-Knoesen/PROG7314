package com.example.prog7314_mobile_app_v2.Retrofit

import com.example.prog7314_mobile_app_v2.models.Projects
import com.example.prog7314_mobile_app_v2.models.TestModel
import retrofit2.Call
import retrofit2.http.*

//interface ProjectsApi {
//    @GET("projects")
//    fun getAllProjects(): Call<List<Projects>>
//
//    @GET("projects/{id}")
//    fun getProjectById(@Path("id") id: String): Call<Projects>
//
//    @POST("projects")
//    fun createProject(@Body project: Projects): Call<Projects>
//
//    @PUT("projects/{id}")
//    fun updateProject(@Path("id") id: String, @Body project: Projects): Call<Void>
//
//    @DELETE("projects/{id}")
//    fun deleteProject(@Path("id") id: String): Call<Void>
//}

interface ProjectsApi {
    @GET("projects")
    fun getAllProjects(): Call<List<TestModel>>

    @GET("projects/{id}")
    fun getProjectById(@Path("id") id: String): Call<TestModel>

    @POST("projects")
    fun createProject(@Body project: TestModel): Call<TestModel>
}
