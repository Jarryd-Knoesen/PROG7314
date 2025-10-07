package com.example.prog7314_mobile_app_v2.models

class UserModel (
    val id: String? = null,
    val firstName: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val uid: String? = null,
    val signInMethod: String? = null,
    val preferences: Map<String, String>? = null
)