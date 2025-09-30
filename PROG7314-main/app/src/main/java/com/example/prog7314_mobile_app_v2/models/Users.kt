package com.example.prog7314_mobile_app_v2.models

import java.io.Serializable

data class Users (
    val userID: String,
    val name: String,
    val email: String,
    val password: String
) : Serializable
