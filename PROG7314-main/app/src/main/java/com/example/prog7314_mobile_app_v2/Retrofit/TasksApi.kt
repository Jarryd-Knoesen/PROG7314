package com.example.prog7314_mobile_app_v2.Retrofit

import com.example.prog7314_mobile_app_v2.models.CreateTaskModel
import com.example.prog7314_mobile_app_v2.models.TaskModel
import com.example.prog7314_mobile_app_v2.models.UpdateTaskModel
import retrofit2.Call
import retrofit2.http.*

interface TasksApi {
    @GET("projects/{projectId}/tasks")
    fun getTasksByProject(@Path("projectId") projectId: String): Call<List<TaskModel>>

    @GET("projects/{projectId}/tasks/{id}")
    fun getTaskById(@Path("projectId") projectId: String, @Path("id") id: String): Call<TaskModel>

    @POST("projects/{projectId}/tasks")
    fun createTask(@Path("projectId") projectId: String,@Body task: CreateTaskModel): Call<TaskModel>

    @PUT("projects/{projectId}/tasks/{taskId}")
    fun updateTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Body updateTask: UpdateTaskModel
    ): Call<Void>

    @DELETE("projects/{projectId}/tasks/{taskId}")
    fun deleteTask(@Path("projectId") projectId: String, @Path("taskId") taskId: String): Call<Void>
}
