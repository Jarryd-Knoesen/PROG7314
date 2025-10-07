package com.example.prog7314_mobile_app_v2.models

data class UpdateTaskModel(
    val name: String?,
    val description: String?,
    val assignedTo: String?,
    val colorStatus: String?,
    val status: String?,
    val dueDate: String?
)