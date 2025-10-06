package com.example.prog7314_mobile_app_v2.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class ProjectModel(
    @SerializedName("id")
    val projectID: String,
    val name: String,
    val description: String,
    val members: List<String>,
    val createdAt: Date? // Nullable now
) : Serializable
