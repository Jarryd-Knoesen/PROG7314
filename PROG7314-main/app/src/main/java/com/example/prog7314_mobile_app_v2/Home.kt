package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskAdapter
import com.example.prog7314_mobile_app_v2.models.Task
import com.example.prog7314_mobile_app_v2.models.TaskRepository
import java.util.Calendar

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawerLayout)

        // Gets ID of the menu items
        val smProfile = findViewById<TextView>(R.id.smProfile)
        val smProjects = findViewById<TextView>(R.id.smProjects)
        val smTaskOverview = findViewById<TextView>(R.id.smTaskOverview)
//
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menu Items click listeners
        smProfile.setOnClickListener {
//            replaceFragment()
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        smProjects.setOnClickListener {
            replaceFragment(FragmentProjects())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        smTaskOverview.setOnClickListener {
//            replaceFragment()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        if (savedInstanceState == null) {
            replaceFragment(FragmentProjects())
        }

    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}