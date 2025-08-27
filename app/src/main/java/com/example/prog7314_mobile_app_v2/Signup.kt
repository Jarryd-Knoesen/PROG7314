package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
        val firstNameInput = findViewById<TextView>(R.id.edFirstName)
        val surnameInput = findViewById<TextView>(R.id.edSurname)
        val phoneNumberInput = findViewById<TextView>(R.id.edPhoneNumber)
        val emailInput = findViewById<TextView>(R.id.etEmail)
        val passwordInput = findViewById<TextView>(R.id.etPassword)

        val signupButton = findViewById<Button>(R.id.btnSignup)
        val loginButton = findViewById<TextView>(R.id.tvLogin)

        loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        signupButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val firstName = firstNameInput.text.toString()
            val surname = surnameInput.text.toString()
            val phoneNumber = phoneNumberInput.text.toString()

            if (!isValidName(firstName)) {
                firstNameInput.error = "Please enter your first name\nYour name cannot contain a number!!!"
                return@setOnClickListener
            }

            if (!isValidName(surname)) {
                surnameInput.error = "Please enter your surname\nYour name cannot contain a number!!!"
                return@setOnClickListener
            }

            if (!isValidPhoneNumber(phoneNumber)) {
                phoneNumberInput.error = "Please enter a valid phone number"
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                emailInput.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                passwordInput.error = "Password must contain 8+ characters, 1 upper, 1 lower, 1 number, and 1 symbol"
                return@setOnClickListener
            }

            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }

    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.all { it.isLetter() }
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(regex.toRegex())
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val regex = "^\\d{10}$"
        return phoneNumber.matches(regex.toRegex())
    }
}