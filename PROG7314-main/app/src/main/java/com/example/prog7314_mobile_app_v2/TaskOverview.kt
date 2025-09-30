package com.example.prog7314_mobile_app_v2

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskOverviewAdapter
import com.example.prog7314_mobile_app_v2.models.Task
import com.example.prog7314_mobile_app_v2.models.TaskRepository
import com.google.android.material.navigation.NavigationView
import java.util.*

class TaskOverview : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskOverviewAdapter
    private lateinit var taskList: List<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_overview)

        drawerLayout = findViewById(R.id.drawerLayout)
        recyclerView = findViewById(R.id.taskOverviewRecyclerView)
        val btnFilter = findViewById<Button>(R.id.btnFilter)
        val navView = findViewById<NavigationView>(R.id.navView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Example data
        taskList = TaskRepository.tasks

        adapter = TaskOverviewAdapter(taskList)
        recyclerView.adapter = adapter

        // Open drawer when button clicked
        btnFilter.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle filter menu selection
        navView.setNavigationItemSelectedListener { menuItem ->
            applyFilter(menuItem)
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun applyFilter(menuItem: MenuItem) {
        val filtered = when (menuItem.itemId) {
            R.id.filter_todo -> taskList.filter { it.colorStatus == 0xFFB0B0B0.toInt() }
            R.id.filter_doing -> taskList.filter { it.colorStatus == 0xFF007BFF.toInt() }
            R.id.filter_completed -> taskList.filter { it.colorStatus == 0xFF28A745.toInt() }
            R.id.filter_issue -> taskList.filter { it.colorStatus == 0xFFDC3545.toInt() }
            else -> taskList
        }
        adapter = TaskOverviewAdapter(filtered)
        recyclerView.adapter = adapter
    }
}
