package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.ProjectsApi
import com.example.prog7314_mobile_app_v2.adapters.ProjectAdapter
import com.example.prog7314_mobile_app_v2.models.ProjectModel
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentProjects : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectAdapter
    private var projectsList: MutableList<ProjectModel> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_projects_view, container, false)

        recyclerView = view.findViewById(R.id.taskProjectCardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val btnCreateProject = view.findViewById<Button>(R.id.btnCreateProject)
        btnCreateProject.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentCreateProject())
                .addToBackStack(null)
                .commit()

            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        adapter = ProjectAdapter(
            projectsList,
            { project -> openProjectTasks(project) },
            { project -> showDeleteConfirmation(project) }
        )

        recyclerView.adapter = adapter

        // 🔹 Fetch from API
        fetchProjectsFromApi()

        return view
    }

    private fun fetchProjectsFromApi() {
        val api = ApiClient.instance.create(ProjectsApi::class.java)
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        api.getAllProjects().enqueue(object : Callback<List<ProjectModel>> {
            override fun onResponse(call: Call<List<ProjectModel>>, response: Response<List<ProjectModel>>) {
                if (response.isSuccessful) {
                    val projects = response.body()
                    if (projects != null) {
                        projectsList.clear()
                        projectsList.addAll(projects.sortedBy { it.name })
                        adapter.updateProjects(projectsList)
                        Log.d("API_SUCCESS", "Loaded ${projectsList.size} projects for user")
                    }
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProjectModel>>, t: Throwable) {
                Log.e("API_FAILURE", "Failed to fetch projects: ${t.message}")
            }
        })
    }

    private fun openProjectTasks(project: ProjectModel) {
        val fragmentTasks = FragmentTasks()
        val bundle = Bundle().apply {
            putString("projectID", project.projectID)
            putString("projectName", project.name)
        }
        fragmentTasks.arguments = bundle

        Log.e("Project Variables Passed Over", "Project ID: ${project.projectID}")
        Log.d("Project Variables Passed Over", "Project Name: ${project.name}")

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmentTasks)
            .addToBackStack(null)
            .commit()
    }

    private fun showDeleteConfirmation(project: ProjectModel) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_project_title))
            .setMessage(
                getString(R.string.delete_project_message_part_1, project.name) +
                        getString(R.string.delete_project_message_part_2)
            )
            .setPositiveButton(getString(R.string.delete_project_button_yes)) { _, _ ->
                deleteProjectFromApi(project)
            }
            .setNegativeButton(getString(R.string.delete_project_button_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteProjectFromApi(project: ProjectModel) {
        val api = ApiClient.instance.create(ProjectsApi::class.java)

        api.deleteProject(project.projectID).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Remove project from local list
                    projectsList.removeIf { it.projectID == project.projectID }
                    adapter.updateProjects(projectsList)
                    Log.d("API_SUCCESS", "Deleted project ${project.projectID}")
                } else {
                    Log.e("API_ERROR", "Failed to delete project: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API_FAILURE", "Error deleting project: ${t.message}")
            }
        })
    }


}
