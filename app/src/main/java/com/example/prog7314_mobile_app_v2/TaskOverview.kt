package com.example.prog7314_mobile_app_v2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskOverviewAdapter
import com.example.prog7314_mobile_app_v2.models.Task
import java.util.Date

class TaskOverview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_overview)

        val recyclerView = findViewById<RecyclerView>(R.id.taskOverviewRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Example dummy data
        val taskList = listOf(
            Task("1", "Design UI", "Make overview screen", Date(),"Jarryd", 0xFF007BFF.toInt()),
            Task("2", "Write API", "Backend for tasks", Date(), "Bobby",0xFF28A745.toInt())
        )

        recyclerView.adapter = TaskOverviewAdapter(taskList)
    }
}
