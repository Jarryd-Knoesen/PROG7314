package com.example.prog7314_mobile_app_v2.models

import java.io.Serializable
import java.util.Date

data class Projects (
    val projectID: String,
    val name: String,
    val members: List<String>
) : Serializable

//data class Projects(
//    val id: String? = null,
//    val name: String? = null,
//    val description: String? = null,
//    val createdBy: String? = null,
//    val members: List<String>? = null,
//    val createdAt: Date? = null
//) : Serializable