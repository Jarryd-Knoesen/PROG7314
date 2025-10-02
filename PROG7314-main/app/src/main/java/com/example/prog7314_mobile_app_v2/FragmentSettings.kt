package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
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
import java.util.Locale
import kotlin.collections.indexOf

class FragmentSettings : Fragment() {
    private var selectedLanguageCode: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // ------------------------
        // General UI Elements
        // ------------------------
        val btnBack = view.findViewById<Button>(R.id.btnBack)
        val swDarkMode = view.findViewById<SwitchCompat>(R.id.swDarkMode)
        val spLanguages = view.findViewById<Spinner>(R.id.spLanguage)
        val btnSave = view.findViewById<Button>(R.id.btnSaveSettings)
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", 0)

        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // ------------------------
        // Dark Mode Section
        // ------------------------
        setupDarkMode(swDarkMode, sharedPref)

        // ------------------------
        // Language Spinner Section
        // ------------------------
        setupLanguageSpinner(spLanguages, sharedPref)

        // Save button for language
        btnSave.setOnClickListener {
            selectedLanguageCode?.let { lang ->
                saveLanguagePreference(lang)
                updateLocale(lang)
            }
        }

        return view
    }

    // ------------------------
    // Dark Mode Functions
    // ------------------------
    private fun setupDarkMode(swDarkMode: SwitchCompat, sharedPref: SharedPreferences) {
        val isDarkMode = sharedPref.getBoolean("DarkMode", true)
        swDarkMode.isChecked = isDarkMode

        swDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(false)
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

        // Set initial selection
        val savedLanguage = sharedPref.getString("Language", "en")
        spLanguages.setSelection(languagesCodes.indexOf(savedLanguage))
        selectedLanguageCode = savedLanguage

        spLanguages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Only store selection temporarily, apply on Save button click
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

        // Restart activity to apply new language
        requireActivity().recreate()
    }
}