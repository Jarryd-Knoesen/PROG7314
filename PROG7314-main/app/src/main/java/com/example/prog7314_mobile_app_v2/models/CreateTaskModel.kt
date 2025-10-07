package com.example.prog7314_mobile_app_v2.models

data class CreateTaskModel(
    val name: String,
    val description: String,
    val dueDate: String,  // ISO 8601 format, e.g., "2025-10-07T00:00:00Z"
    val assignedTo: String,
    val colorStatus: String,
    val status: String
)
