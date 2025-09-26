package com.example.prog7314_mobile_app_v2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // <-- from google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val firstNameInput = findViewById<EditText>(R.id.edFirstName)
        val surnameInput = findViewById<EditText>(R.id.edSurname)
        val phoneInput = findViewById<EditText>(R.id.edPhoneNumber)
        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val signupButton = findViewById<Button>(R.id.btnSignup)
        val loginText = findViewById<TextView>(R.id.tvLogin)
        val googleButton = findViewById<LinearLayout>(R.id.btnGoogleSignup)

        // Email + Password Signup
        signupButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val surname = surnameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (firstName.isEmpty()) {
                firstNameInput.error = "Enter first name"
                return@setOnClickListener
            }
            if (surname.isEmpty()) {
                surnameInput.error = "Enter surname"
                return@setOnClickListener
            }
            if (phone.isEmpty() || phone.length < 7) {
                phoneInput.error = "Enter valid phone number"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Enter valid email"
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordInput.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Navigate to Login
        loginText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Google Signup/Login
        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Instead of going to Home, go to Login page
                            Toast.makeText(this, "Signup successful. Please login with Google.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_LONG).show()
                        }
                    }
            } catch (e: Exception) {
                Toast.makeText(this, "Google sign-in error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }


        googleButton.setOnClickListener {
            // Sign out previous Google session to force account picker
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            }
        }
    }
}
