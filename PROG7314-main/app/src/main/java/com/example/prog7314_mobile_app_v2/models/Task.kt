package com.example.prog7314_mobile_app_v2.models

import java.io.Serializable
import java.util.Date

data class Task(
    val taskID: String,
    val name: String,
    val description: String,
    val dueDate: Date,  // Now using Date type
    val assignedTo: String,
    val colorStatus: Int,
    val status: String,
    val projectID: String // Links to a project
) : Serializable

//data class Task(
//    val id: String? = null,
//    val name: String? = null,
//    val description: String? = null,
//    val assignedTo: String? = null,
//    val colorStatus: String? = null,
//    val status: String? = null,
//    val dueDate: Date? = null,
//    val createdAt: Date? = null
//) : Serializable