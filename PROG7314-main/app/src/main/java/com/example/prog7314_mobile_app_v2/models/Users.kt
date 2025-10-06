package com.example.prog7314_mobile_app_v2.models

import java.io.Serializable
import java.util.Date

data class Users (
    val userID: String,
    val name: String,
    val email: String,
    val password: String
) : Serializable

//data class Users(
//    val id: String? = null,
//    val email: String? = null,
//    val firstName: String? = null,
//    val surname: String? = null,
//    val phone: String? = null,
//    val signInMethod: String? = null,
//    val createdAt: Date? = null,
//    val preferences: Preferences? = null
//) : Serializable {
//    data class Preferences(
//        val language: String? = null,
//        val theme: String? = null
//    ) : Serializable
//}