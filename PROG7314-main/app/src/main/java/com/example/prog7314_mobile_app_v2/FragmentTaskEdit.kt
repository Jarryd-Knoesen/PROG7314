package com.example.prog7314_mobile_app_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.prog7314_mobile_app_v2.models.ProjectsRepository
import com.example.prog7314_mobile_app_v2.models.Task
import java.util.Calendar

class FragmentTaskEdit : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_edit, container, false)

        val task = arguments?.getSerializable("task") as? Task

        task?.let {
            // Look up the project name using projectID
            val projectName = ProjectsRepository.projects
                .firstOrNull { project -> project.projectID == it.projectID }
                ?.name ?: "Unknown Project"

            // Project and task details
            view.findViewById<TextView>(R.id.txtProjectName).text = getString(R.string.task_project_name, projectName)
            view.findViewById<TextView>(R.id.txtTaskName).text = it.name
            view.findViewById<TextView>(R.id.txtDescription).text = it.description
            view.findViewById<TextView>(R.id.txtAssignedTo).text = getString(R.string.task_assigned_to, it.assignedTo)

            // Date -> split into day, month, year
            val calendar = Calendar.getInstance()
            calendar.time = it.dueDate
            view.findViewById<EditText>(R.id.editTextDay).hint = calendar.get(Calendar.DAY_OF_MONTH).toString()
            view.findViewById<EditText>(R.id.editTextMonth).hint = (calendar.get(Calendar.MONTH) + 1).toString()
            view.findViewById<EditText>(R.id.editTextYear).hint = calendar.get(Calendar.YEAR).toString()

            // Spinner -> set current status
            val spinner = view.findViewById<Spinner>(R.id.spinnerStatus)
            val statuses = listOf(getString(R.string.status_in_que_to_do), getString(R.string.status_doing), getString(R.string.status_completed), getString(R.string.status_issue_stuck)) // Statuses
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val currentIndex = statuses.indexOf(it.status)
            if (currentIndex >= 0) {
                spinner.setSelection(currentIndex)
            }
        }

        return view
    }
}