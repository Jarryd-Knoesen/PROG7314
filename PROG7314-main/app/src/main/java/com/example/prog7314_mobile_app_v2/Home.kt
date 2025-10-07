package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.UsersApi
import com.example.prog7314_mobile_app_v2.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val TAG = "Home"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawerLayout)

        // Get menu items
        val smProfile = findViewById<TextView>(R.id.smProfile)
        val smProjects = findViewById<TextView>(R.id.smProjects)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        smProfile.setOnClickListener {
            replaceFragment(FragmentProfile())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        smProjects.setOnClickListener {
            replaceFragment(FragmentProjects())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        if (savedInstanceState == null) {
            replaceFragment(FragmentProjects())
        }

        // Apply user theme/language if needed
        applyUserPreferences()
    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun applyUserPreferences() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e(TAG, "No user logged in.")
            return
        }

        val userId = user.uid
        val api = ApiClient.instance.create(UsersApi::class.java)

        api.getUserById(userId).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (!response.isSuccessful) {
                    Log.e(TAG, "Failed to fetch user: ${response.code()} ${response.message()}")
                    return
                }

                val userData = response.body()
                Log.d(TAG, "User data: $userData")

                // Extract preferences
                val prefs = userData?.preferences
                val language = prefs?.get("language") ?: "en"
                val theme = prefs?.get("theme") ?: "Light"
                Log.d(TAG, "Language: $language, Theme: $theme")

                // Apply theme
                when (theme.lowercase(Locale.ROOT)) {
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                // Apply language if not English
                if (language != "en") {
                    val locale = Locale(language)
                    Locale.setDefault(locale)
                    val config = resources.configuration
                    config.setLocale(locale)
                    baseContext.resources.updateConfiguration(config, resources.displayMetrics)
                    recreate()
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.e(TAG, "Error fetching user data", t)
            }
        })
    }
}
