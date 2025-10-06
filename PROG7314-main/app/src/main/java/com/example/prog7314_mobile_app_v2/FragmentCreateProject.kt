package com.example.prog7314_mobile_app_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.ProjectsApi
import com.example.prog7314_mobile_app_v2.models.CreateProjectModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCreateProject : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_project, container, false)

        val btnCreateProject = view.findViewById<Button>(R.id.btnCreateProject)
        val etProjectName = view.findViewById<EditText>(R.id.etProjectName)

        btnCreateProject.setOnClickListener {
            val projectName = etProjectName.text.toString().trim()

            if (projectName.isNotEmpty()) {
                createProject(projectName)
            }
        }

        return view
    }

    private fun createProject(name: String) {
        val api = ApiClient.instance.create(ProjectsApi::class.java)
        val request = CreateProjectModel(name, "")

        api.createProject(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("✅ Project created successfully")

                    // Go back to project list
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FragmentProjects())
                        .addToBackStack(null)
                        .commit()

                    (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

                } else {
                    println("⚠ Error creating project: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("❌ API call failed: ${t.message}")
            }
        })
    }
}
