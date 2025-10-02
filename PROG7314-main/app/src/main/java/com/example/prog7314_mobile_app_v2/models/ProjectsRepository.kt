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

    fun addProject(project: Projects) {
        projects.add(project)
    }

    fun generateProjectID(): String {
        val nextId = projects.size + 1
        return "P%03d".format(nextId) // e.g., P006
    }

    fun addMemberToProject(projectID: String, memberEmail: String): Boolean {
        val index = projects.indexOfFirst{ it.projectID == projectID }
        if (index != -1) {
            val project = projects[index]
            if (!project.members.contains(memberEmail)) {
                projects[index] = project.copy(members = project.members + memberEmail)
                return true
            }
        }
        return false
    }

}