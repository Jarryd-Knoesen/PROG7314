package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.UsersApi
import com.example.prog7314_mobile_app_v2.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentProfile : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnSettings: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.tvProfileName)
        tvEmail = view.findViewById(R.id.tvProfileEmail)
        btnSettings = view.findViewById(R.id.btnSettings)

        btnSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentSettings())
                .addToBackStack(null)
                .commit()

            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        loadUserProfile()

        return view
    }

    private fun loadUserProfile() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            tvName.text = "Unknown User"
            tvEmail.text = "Not logged in"
            return
        }

        val usersApi = ApiClient.instance.create(UsersApi::class.java)
        val call = usersApi.getUserById(currentUserId)

        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val fullName = "${user.firstName ?: ""} ${user.surname ?: ""}".trim()
                        tvName.text = if (fullName.isNotEmpty()) fullName else "No name available"
                        tvEmail.text = user.email ?: "No email"
                    } else {
                        tvName.text = "User not found"
                        tvEmail.text = ""
                    }
                } else {
                    tvName.text = "Error loading user"
                    tvEmail.text = ""
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                tvName.text = "Failed to load profile"
                tvEmail.text = ""
            }
        })
    }
}
