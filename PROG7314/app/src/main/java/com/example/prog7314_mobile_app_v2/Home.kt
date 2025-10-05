package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskAdapter
import com.example.prog7314_mobile_app_v2.models.Task
import com.example.prog7314_mobile_app_v2.models.TaskRepository
import java.util.Calendar

class Home : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Temp list of tasks
        val tasks = TaskRepository.tasks.sortedBy { it.dueDate }

        val recyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TaskAdapter(tasks, this)

        // Gets ID of the menu button and the drawer layout
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLoyout)
        val btnMenu = findViewById<Button>(R.id.btnMenu)

        val btnTasks = findViewById<TextView>(R.id.smTaskOverview)

        // Gets ID of the menu items
        val smProfile = findViewById<TextView>(R.id.smProfile)
        val smEditTasks = findViewById<TextView>(R.id.smEditTasks)

        // Opens the drawer when the menu button is clicked
        btnMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.menuLayout))) {
                drawerLayout.closeDrawer(findViewById(R.id.menuLayout))
            } else {
                drawerLayout.openDrawer(findViewById(R.id.menuLayout))
            }
        }

        // Menu Items click listeners
        smProfile.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        smEditTasks.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnTasks.setOnClickListener {
            startActivity(Intent(this, TaskOverview::class.java))
        }

    }

    private fun getDate(year: Int, month: Int, day: Int): java.util.Date {
        return Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}