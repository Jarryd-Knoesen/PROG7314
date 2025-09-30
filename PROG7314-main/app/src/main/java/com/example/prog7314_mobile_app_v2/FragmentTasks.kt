package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskAdapter
import com.example.prog7314_mobile_app_v2.models.Task
import com.example.prog7314_mobile_app_v2.models.TaskRepository
import java.text.SimpleDateFormat
import java.util.Locale

class FragmentTasks : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var tasksList: List<Task>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_tasks_view, container, false)

        val projectID = arguments?.getString("projectID")
        val projectName = arguments?.getString("projectName")

        view.findViewById<TextView>(R.id.txtProjectName).text = projectName

        recyclerView = view.findViewById(R.id.taskTaskCardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tasksList = TaskRepository.tasks
            .filter { it.projectID == projectID }
            .sortedBy { it.dueDate }

        adapter = TaskAdapter(tasksList) { task ->
            showTaskDialog(task)
        }
        recyclerView.adapter = adapter

        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun showTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.task_dialog_task_details, null)

        val taskNameText = dialogView.findViewById<TextView>(R.id.dialogTaskName)
        val taskDescriptionText = dialogView.findViewById<TextView>(R.id.dialogTaskDescription)
        val taskDueDateText = dialogView.findViewById<TextView>(R.id.dialogTaskDueDate)
        val taskAssignedToText = dialogView.findViewById<TextView>(R.id.dialogTaskAssignedTo)
        val taskStatus = dialogView.findViewById<TextView>(R.id.dialogTaskStatus)

        taskNameText.text = task.name
        taskDescriptionText.text = task.description
        taskDueDateText.text = "Due: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(task.dueDate)}"
        taskAssignedToText.text = "Assigned to: ${task.assignedTo}"
        taskStatus.text = "Status: ${task.status}"

        builder.setView(dialogView)
            .setNegativeButton("Edit") { _, _ ->
                val fragmentTaskEdit = FragmentTaskEdit()
                val bundle = Bundle()
                bundle.putSerializable("task", task)
                fragmentTaskEdit.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentTaskEdit)
                    .addToBackStack(null)
                    .commit()
            }
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}