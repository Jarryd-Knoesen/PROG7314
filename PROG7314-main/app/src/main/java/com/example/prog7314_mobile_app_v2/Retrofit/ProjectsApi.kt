package com.example.prog7314_mobile_app_v2.Retrofit

import com.example.prog7314_mobile_app_v2.models.Projects
import com.example.prog7314_mobile_app_v2.models.Task
import retrofit2.Call
import retrofit2.http.*

interface ProjectsApi {

    // --- Projects ---
    @GET("projects")
    fun getAllProjects(): Call<List<Projects>>

    @GET("projects/{id}")
    fun getProjectById(@Path("id") id: String): Call<Projects>

    @POST("projects")
    fun createProject(@Body project: Projects): Call<Projects>

    @PUT("projects/{id}")
    fun updateProject(@Path("id") id: String, @Body project: Projects): Call<Void>

    @DELETE("projects/{id}")
    fun deleteProject(@Path("id") id: String): Call<Void>


    // --- Tasks ---
    @GET("projects/{projectId}/tasks")
    fun getAllTasks(@Path("projectId") projectId: String): Call<List<Task>>

    @GET("projects/{projectId}/tasks/{id}")
    fun getTaskById(
        @Path("projectId") projectId: String,
        @Path("id") id: String
    ): Call<Task>

    @POST("projects/{projectId}/tasks")
    fun createTask(
        @Path("projectId") projectId: String,
        @Body task: Task
    ): Call<Task>

    @PUT("projects/{projectId}/tasks/{taskId}")
    fun updateTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Body task: Task
    ): Call<Void>

    @DELETE("projects/{projectId}/tasks/{taskId}")
    fun deleteTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String
    ): Call<Void>
}
