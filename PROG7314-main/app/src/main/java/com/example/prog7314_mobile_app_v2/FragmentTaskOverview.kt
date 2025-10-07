package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskOverviewAdapter
import com.example.prog7314_mobile_app_v2.models.TaskModel
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.TasksApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentTaskOverview : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskOverviewAdapter
    private var taskList: List<TaskModel> = emptyList()
    private lateinit var searchText: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_overview, container, false)

        searchText = view.findViewById(R.id.etFilterSearch)
        recyclerView = view.findViewById(R.id.taskOverviewRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val projectID = arguments?.getString("projectID")
        val projectName = arguments?.getString("projectName")

        getString(R.string.project_task_overview, projectName)
            .also { view.findViewById<TextView>(R.id.txtProjectName).text = it }

        adapter = TaskOverviewAdapter(emptyList())
        recyclerView.adapter = adapter

        if (projectID != null) {
            fetchTasks(projectID)
        } else {
            Toast.makeText(requireContext(), "Project ID not found", Toast.LENGTH_SHORT).show()
        }

        setupSortHeader(view.findViewById(R.id.header_Task)) { it.name }
        setupSortHeader(view.findViewById(R.id.header_status)) { it.status }
        setupSortHeader(view.findViewById(R.id.header_due_date)) { it.dueDate }
        setupSortHeader(view.findViewById(R.id.header_assigned_to)) { it.assignedTo }
        setupSortHeader(view.findViewById(R.id.header_description)) { it.description ?: "" }

        searchText.addTextChangedListener { query ->
            val filteredList = taskList.filter { task ->
                task.name.contains(query.toString(), ignoreCase = true) ||
                        task.status.contains(query.toString(), ignoreCase = true) ||
                        (task.dueDate?.toString()?.contains(query.toString(), ignoreCase = true) ?: false) ||
                        task.assignedTo.contains(query.toString(), ignoreCase = true) ||
                        (task.description?.contains(query.toString(), ignoreCase = true) ?: false)
            }
            adapter.updateSearchList(filteredList)
        }


        return view
    }

    private fun fetchTasks(projectID: String) {
        val api = ApiClient.instance.create(TasksApi::class.java)
        api.getTasksByProject(projectID).enqueue(object : Callback<List<TaskModel>> {
            override fun onResponse(
                call: Call<List<TaskModel>>,
                response: Response<List<TaskModel>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    taskList = response.body()!!
                    adapter.updateList(taskList)
                } else {
                    Toast.makeText(requireContext(), "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TaskModel>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun <T : Comparable<T>> setupSortHeader(
        headerView: TextView,
        selector: (TaskModel) -> T
    ) {
        var ascending = true
        headerView.setOnClickListener {
            val sorted = if (ascending) {
                taskList.sortedBy(selector)
            } else {
                taskList.sortedByDescending(selector)
            }
            ascending = !ascending
            adapter.updateList(sorted)
        }
    }
}
