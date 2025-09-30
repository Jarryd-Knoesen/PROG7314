package com.example.prog7314_mobile_app_v2.models

object UsersRepository {
    val users: MutableList<Users> = mutableListOf(
        Users(
            userID = "alice@example.com",
            name = "Alice",
            email = "alice@example.com",
            password = "password123"
        ),
        Users(
            userID = "bob@example.com",
            name = "Bob",
            email = "bob@example.com",
            password = "password123"
        ),
        Users(
            userID = "charlie@example.com",
            name = "Charlie",
            email = "charlie@example.com",
            password = "password123"
        ),
        Users(
            userID = "hannah@example.com",
            name = "Hannah",
            email = "hannah@example.com",
            password = "password123v"
        ),
        Users(
            userID = "kara@example.com",
            name = "Kara",
            email = "kara@example.com",
            password = "password123"
        )
    )

}