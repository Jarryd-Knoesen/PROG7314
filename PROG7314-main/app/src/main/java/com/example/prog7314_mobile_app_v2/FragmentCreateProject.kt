package com.example.prog7314_mobile_app_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.prog7314_mobile_app_v2.models.Projects
import com.example.prog7314_mobile_app_v2.models.ProjectsRepository

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
                val newProject = Projects(
                    projectID = ProjectsRepository.generateProjectID(),
                    name = projectName,
                    members = emptyList()
                )
                ProjectsRepository.addProject(newProject)
            }

            // Navigate back to projects list
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentProjects())
                .addToBackStack(null)
                .commit()

            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        return view
    }
}