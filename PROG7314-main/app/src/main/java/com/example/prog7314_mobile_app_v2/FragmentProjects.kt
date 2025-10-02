package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.ProjectAdapter
import com.example.prog7314_mobile_app_v2.models.Projects
import com.example.prog7314_mobile_app_v2.models.ProjectsRepository

class FragmentProjects : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectAdapter
    private lateinit var projectsList: List<Projects>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        val view = inflater.inflate(R.layout.fragment_projects_view, container, false)

        // RecyclerView setup
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

        // Load projects
        projectsList = ProjectsRepository.projects.sortedBy { it.name }

        // Adapter with click and delete handlers
        adapter = ProjectAdapter(
            projectsList,
            { project -> // On project click
                val fragmentTasks = FragmentTasks()
                val bundle = Bundle()
                bundle.putString("projectID", project.projectID)
                bundle.putString("projectName", project.name)
                fragmentTasks.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentTasks)
                    .addToBackStack(null)
                    .commit()
            },
            { project -> // On delete click
                showDeleteConfirmation(project)
            }
        )

        recyclerView.adapter = adapter
        return view
    }

    private fun showDeleteConfirmation(project: Projects) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Project")
        builder.setMessage("Are you sure you want to delete \"${project.name}\"? This action cannot be undone.")
        builder.setPositiveButton("Yes") { _, _ ->
            ProjectsRepository.projects.removeIf { it.projectID == project.projectID }
            projectsList = ProjectsRepository.projects.sortedBy { it.name }
            adapter.updateProjects(projectsList)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
