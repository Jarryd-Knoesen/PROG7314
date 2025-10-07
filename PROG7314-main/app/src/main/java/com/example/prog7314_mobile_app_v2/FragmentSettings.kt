package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.UsersApi
import com.example.prog7314_mobile_app_v2.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class FragmentSettings : Fragment() {

    private var selectedLanguageCode: String? = null
    private var isDarkMode: Boolean = false
    private var currentUserData: UserModel? = null

    private val api by lazy { ApiClient.instance.create(UsersApi::class.java) }
    private val firebaseUser by lazy { FirebaseAuth.getInstance().currentUser }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnBack = view.findViewById<Button>(R.id.btnBack)
        val swDarkMode = view.findViewById<SwitchCompat>(R.id.swDarkMode)
        val spLanguages = view.findViewById<Spinner>(R.id.spLanguage)
        val btnSave = view.findViewById<Button>(R.id.btnSaveSettings)
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", 0)

        // Back button
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        setupDarkMode(swDarkMode, sharedPref)
        setupLanguageSpinner(spLanguages, sharedPref)

        // 🔹 Fetch current user data before allowing updates
        fetchCurrentUserData()

        // Save button — updates everything
        btnSave.setOnClickListener {
            selectedLanguageCode?.let { lang ->
                saveLanguagePreference(lang)
                updateLocale(lang)
                updateFullUserData(lang, if (isDarkMode) "dark" else "light")
            }
        }

        return view
    }

    // ------------------------
    // Fetch current user data
    // ------------------------
    private fun fetchCurrentUserData() {
        val uid = firebaseUser?.uid ?: return

        api.getUserById(uid).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    currentUserData = response.body()
                    Log.d("USER_FETCH_SUCCESS", "Loaded user data: $currentUserData")
                } else {
                    Log.e("USER_FETCH_FAIL", "Error fetching user: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.e("USER_FETCH_ERROR", "Error: ${t.message}")
            }
        })
    }

    // ------------------------
    // Dark Mode Functions
    // ------------------------
    private fun setupDarkMode(swDarkMode: SwitchCompat, sharedPref: SharedPreferences) {
        isDarkMode = sharedPref.getBoolean("DarkMode", false)
        swDarkMode.isChecked = isDarkMode

        swDarkMode.setOnCheckedChangeListener { _, isChecked ->
            isDarkMode = isChecked
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(true)
                updateFullUserData(selectedLanguageCode ?: "en", "dark")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(false)
                updateFullUserData(selectedLanguageCode ?: "en", "light")
            }
        }
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", 0)
        with(sharedPref.edit()) {
            putBoolean("DarkMode", isDarkMode)
            apply()
        }
    }

    // ------------------------
    // Language Spinner Functions
    // ------------------------
    private fun setupLanguageSpinner(spLanguages: Spinner, sharedPref: SharedPreferences) {
        val languages = listOf("English", "Afrikaans")
        val languagesCodes = listOf("en", "af")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLanguages.adapter = adapter

        val savedLanguage = sharedPref.getString("Language", "en")
        spLanguages.setSelection(languagesCodes.indexOf(savedLanguage))
        selectedLanguageCode = savedLanguage

        spLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedLanguageCode = languagesCodes[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun saveLanguagePreference(lang: String) {
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", 0)
        with(sharedPref.edit()) {
            putString("Language", lang)
            apply()
        }
    }

    private fun updateLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

    // ------------------------
    // 🔹 Update Full User Data via API
    // ------------------------
    private fun updateFullUserData(language: String, theme: String) {
        val user = firebaseUser ?: return
        val existingData = currentUserData ?: return

        val updatedUser = UserModel(
            id = existingData.id,
            firstName = existingData.firstName,
            surname = existingData.surname,
            email = existingData.email,
            phone = existingData.phone,
            uid = existingData.uid ?: user.uid,
            signInMethod = existingData.signInMethod ?: "password",
            preferences = mapOf(
                "language" to language,
                "theme" to theme
            )
        )

        Log.d("USER_UPDATE", "Updating full user data: $updatedUser")

        api.updateUser(user.uid, updatedUser).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("USER_UPDATE_SUCCESS", "User updated successfully.")
                } else {
                    Log.e("USER_UPDATE_FAIL", "Failed to update user: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("USER_UPDATE_ERROR", "Error updating user: ${t.message}")
            }
        })
    }
}
