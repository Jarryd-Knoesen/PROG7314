package com.example.prog7314_mobile_app_v2.models

import android.graphics.Color
import java.util.*

object TaskRepository {
    val tasks: MutableList<Task> = mutableListOf(
        Task(
            "T1",
            "Deploy Update",
            "Push latest version to production",
            getDate(2025, 8, 14),
            "Jarryd",
            Color.parseColor("#28A745"),
            "Completed",
            "P001" // belongs to Library Management System
        ),
        Task(
            "T2",
            "Server Backup",
            "Perform full backup of database and application files",
            getDate(2025, 8, 15),
            "Jarryd",
            Color.parseColor("#007BFF"),
            "Doing",
            "P002" // Weather Forecast App
        ),
        Task(
            "T3",
            "Fix Login Bug",
            "Investigate and resolve user login timeout issue",
            getDate(2025, 8, 16),
            "Jarryd",
            Color.parseColor("#007BFF"),
            "Doing",
            "P003" // Online Grocery Platform
        ),
        Task(
            "T4",
            "Set Up Workstations",
            "Prepare and configure laptops for new employees",
            getDate(2025, 8, 17),
            "Jarryd",
            Color.parseColor("#DC3545"),
            "Issue / Stuck",
            "P004" // Fitness Tracker
        ),
        Task(
            "T5",
            "Network Audit",
            "Review firewall rules and security settings",
            getDate(2025, 8, 18),
            "Jarryd",
            Color.parseColor("#B0B0B0"),
            "In Que To Start",
            "P005" // Smart Home Controller
        ),
        Task(
            "T6",
            "Test Workstations",
            "Test laptops for new employees",
            getDate(2025, 8, 17),
            "Jarryd",
            Color.parseColor("#007BFF"),
            "Doing",
            "P004" // Fitness Tracker
        )
    )

    private fun getDate(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}
