package com.example.prog7314_mobile_app_v2.Retrofit

import com.example.prog7314_mobile_app_v2.models.CreateProjectModel
import com.example.prog7314_mobile_app_v2.models.ProjectModel
import retrofit2.Call
import retrofit2.http.*

interface ProjectsApi {
    @GET("projects/user/{userUid}")
    fun getUserProjects(@Path("userUid") userUid: String): Call<List<ProjectModel>>

    @GET("projects/{id}")
    fun getProjectById(@Path("id") id: String): Call<ProjectModel>

    @POST("projects")
    fun createProject(@Body project: CreateProjectModel): Call<Void>

    @PUT("projects/{id}")
    fun updateProject(@Path("id") id: String, @Body project: ProjectModel): Call<Void>

    @DELETE("projects/{id}")
    fun deleteProject(@Path("id") id: String): Call<Void>
}
