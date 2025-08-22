package com.example.prog7314_mobile_app_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Patterns

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signupButton = findViewById<TextView>(R.id.tvSignup)

        loginButton.setOnClickListener {

            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (!isUserValid(password, email)) {
                emailInput.error = "Email or Password is invalid"
                return@setOnClickListener
            }

            startActivity(Intent(this, Home::class.java))
            finish()
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
            finish()
        }
    }

    fun isUserValid(password: String, email: String): Boolean {
        // put the code to check the database here
        return true
    }

}