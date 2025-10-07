package com.example.prog7314_mobile_app_v2.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class TaskModel(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val assignedTo: String,
    val colorStatus: String,
    val status: String,
    val dueDate: Date // or Date if you configure a date converter
) : Serializable
