package com.example.prog7314_mobile_app_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_projects_view, container, false)

        recyclerView = view.findViewById(R.id.taskProjectCardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        projectsList = ProjectsRepository.projects.sortedBy { it.name}
        adapter = ProjectAdapter(projectsList) { project ->
            val fragmentTasks = FragmentTasks()

            val bundle = Bundle()
            bundle.putString("projectID", project.projectID)
            bundle.putString("projectName", project.name)
            fragmentTasks.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentTasks)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        return view
    }
}