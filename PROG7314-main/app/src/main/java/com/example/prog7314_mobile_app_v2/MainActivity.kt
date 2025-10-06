package com.example.prog7314_mobile_app_v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.ProjectsApi
import com.example.prog7314_mobile_app_v2.models.Projects
import com.example.prog7314_mobile_app_v2.models.TestModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signupText = findViewById<TextView>(R.id.tvSignup)
        val googleButton = findViewById<LinearLayout>(R.id.btnGoogleLogin)

        // Email/Password login
        loginButton.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
//            val email = emailField.text.toString()
//            val password = passwordField.text.toString()
//
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//                auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Toast.makeText(this, "@string/login_successful", Toast.LENGTH_SHORT).show()
//                            startActivity(Intent(this, Home::class.java))
//                            finish()
//                        } else {
//                            val exception = task.exception
//                            // Check if the email is registered with another provider
//                            if (exception is com.google.firebase.auth.FirebaseAuthInvalidUserException) {
//                                Toast.makeText(
//                                    this,
//                                    "@string/login_no_account_found_error",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            } else if (exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
//                                Toast.makeText(
//                                    this,
//                                    "@string/login_email_exists_error",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            } else {
//                                Toast.makeText(
//                                    this,
//                                    "@string/login_failed ${exception?.message}",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }
//                    }
//            } else {
//                Toast.makeText(this, "@string/login_fields_empty_error", Toast.LENGTH_SHORT).show()
//            }
        }

        // Navigate to Signup
        signupText.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        // Google Sign-In setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google login button
        googleButton.setOnClickListener {
            // Force account picker every time
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            }
        }

        testGetAllProjects()
    }

    // Google login result handler
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(this, "@string/google_sign_in_successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "@string/google_signin_failed", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Sign-In failed: ${e.statusCode}")
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun testGetAllProjects() {
        val api = ApiClient.instance.create(ProjectsApi::class.java)

        api.getAllProjects().enqueue(object : retrofit2.Callback<List<TestModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<TestModel>>,
                response: retrofit2.Response<List<TestModel>>
            ) {
                if (response.isSuccessful) {
                    val projects = response.body()
                    Log.d("API_SUCCESS", "Projects: $projects")
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<TestModel>>, t: Throwable) {
                Log.e("API_FAILURE", "Error: ${t.message}")
            }
        })
    }
}
