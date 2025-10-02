package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskOverviewAdapter
import com.example.prog7314_mobile_app_v2.models.Task
import com.example.prog7314_mobile_app_v2.models.TaskRepository

class FragmentTaskOverview : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskOverviewAdapter
    private lateinit var taskList: List<Task>
    private lateinit var searchText: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_overview, container, false)

        // Filter ID
        searchText = view.findViewById(R.id.etFilterSearch)

        recyclerView = view.findViewById(R.id.taskOverviewRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Example Data
        val projectID = arguments?.getString("projectID")
        val projectName = arguments?.getString("projectName")

        view.findViewById<TextView>(R.id.txtProjectName).text = "$projectName Task Overview"

        taskList = if (projectID != null) {
            TaskRepository.tasks.filter { it.projectID == projectID }
        } else {
            TaskRepository.tasks
        }
        adapter = TaskOverviewAdapter(taskList)
        recyclerView.adapter = adapter

        // Setup sorting headers
        setupSortHeader(view.findViewById(R.id.header_Task)) { it.name}
        setupSortHeader(view.findViewById(R.id.header_status)) { it.status}
        setupSortHeader(view.findViewById(R.id.header_due_date)) { it.dueDate}
        setupSortHeader(view.findViewById(R.id.header_assigned_to)) { it.assignedTo}
        setupSortHeader(view.findViewById(R.id.header_description)) { it.description}

        searchText.addTextChangedListener { query ->
            val filteredList = taskList.filter { task ->
                task.name.contains(query.toString(), ignoreCase = true) ||
                task.status.contains(query.toString(), ignoreCase = true) ||
                task.dueDate.toString().contains(query.toString(), ignoreCase = true) ||
                task.assignedTo.contains(query.toString(), ignoreCase = true) ||
                task.description.contains(query.toString(), ignoreCase = true)
            }
            adapter.updateSearchList(filteredList)
        }

        return view
    }

    private fun <T : Comparable<T>> setupSortHeader (
        headerView: TextView,
        selector: (Task) -> T
    ) {
        var ascending = true
        headerView.setOnClickListener {
            val sorted = if ( ascending ) {
                taskList.sortedBy(selector)
            } else {
                taskList.sortedByDescending(selector)
            }
            ascending = !ascending
            adapter.updateList(sorted)
        }
    }
}