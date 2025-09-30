package com.example.prog7314_mobile_app_v2.models

import java.io.Serializable

data class Projects (
    val projectID: String,
    val name: String,
    val members: List<String>
) : Serializable