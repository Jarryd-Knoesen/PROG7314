package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Gets the user input from the text fields
        val firstName = findViewById<TextView>(R.id.edFirstName)
        val surname = findViewById<TextView>(R.id.edSurname)
        val phoneNumber = findViewById<TextView>(R.id.edPhoneNumber)
        val email = findViewById<TextView>(R.id.etEmail)
        val password = findViewById<TextView>(R.id.etPassword)

        val signupButton = findViewById<Button>(R.id.btnSignup)
        val loginButton = findViewById<TextView>(R.id.tvLogin)

        loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }
}