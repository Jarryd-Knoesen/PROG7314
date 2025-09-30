package com.example.prog7314_mobile_app_v2.models

object ProjectsRepository {
    val projects: MutableList<Projects> = mutableListOf(
        Projects(
            projectID = "P001",
            name = "Library Management System",
            members = listOf("alice@example.com", "bob@example.com", "charlie@example.com")
        ),
        Projects(
            projectID = "P002",
            name = "Weather Forecast App",
            members = listOf("alice@example.com", "charlie@example.com")
        ),
        Projects(
            projectID = "P003",
            name = "Online Grocery Platform",
            members = listOf("alice@example.com", "bob@example.com", "hannah@example.com")
        ),
        Projects(
            projectID = "P004",
            name = "Fitness Tracker",
            members = listOf("alice@example.com", "kara@example.com")
        ),
        Projects(
            projectID = "P005",
            name = "Smart Home Controller",
            members = listOf("alice@example.com", "charlie@example.com", "kara@example.com")
        )
    )

}