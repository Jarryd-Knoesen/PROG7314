package com.example.prog7314_mobile_app_v2.models

import java.io.Serializable
import java.util.Date

data class Task(
    val taskID: String,
    val name: String,
    val description: String,
    val dueDate: Date,  // Now using Date type
    val color: Int
) : Serializable