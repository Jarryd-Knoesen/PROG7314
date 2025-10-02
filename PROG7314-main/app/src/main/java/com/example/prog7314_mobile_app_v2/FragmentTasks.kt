package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.adapters.TaskAdapter
import com.example.prog7314_mobile_app_v2.models.Task
import com.example.prog7314_mobile_app_v2.models.TaskRepository
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import androidx.core.graphics.toColorInt
import com.example.prog7314_mobile_app_v2.models.ProjectsRepository

class FragmentTasks : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var tasksList: List<Task>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_tasks_view, container, false)

        val btnTaskOverview = view.findViewById<Button>(R.id.btnTaskOverview)
        val btnAddMember = view.findViewById<Button>(R.id.btnAddMember)
        val btnAddTask = view.findViewById<Button>(R.id.btnAddTask)

        val projectID = arguments?.getString("projectID")
        val projectName = arguments?.getString("projectName")

        btnAddMember.setOnClickListener {
            showAddMemberDialog(projectID)
        }

        btnAddTask.setOnClickListener {
            showAddTaskDialog(projectID)
        }

        btnTaskOverview.setOnClickListener {
            val fragmentOverview = FragmentTaskOverview()
            val bundle = Bundle()
            bundle.putString("projectID", projectID)
            bundle.putString("projectName", projectName)
            fragmentOverview.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentOverview)
                .addToBackStack(null)
                .commit()
        }

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

    // Shows the task details pop up
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
        "Due: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(task.dueDate)}".also { taskDueDateText.text = it }
        "Assigned to: ${task.assignedTo}".also { taskAssignedToText.text = it }
        "Status: ${task.status}".also { taskStatus.text = it }

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
            .setNeutralButton("Delete") { _, _ ->
                confirmDeleteTask(task)
            }
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }


        val dialog = builder.create()
        dialog.show()
    }

    // Shows the add member pop up
    @SuppressLint("MissingInflatedId")
    private fun showAddMemberDialog(projectID: String?) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.task_add_member, null)

        val emailInput = dialogView.findViewById<EditText>(R.id.etEmail)

        builder.setView(dialogView)
            .setTitle("Add Member")
            .setPositiveButton("Add") { dialog, _ ->
                val email = emailInput.text.toString().trim()

                if (email.isNotEmpty() && projectID != null) {
                    val success = ProjectsRepository.addMemberToProject(projectID, email)
                    if (success) {
                        android.widget.Toast.makeText(requireContext(), "Added $email to project", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(requireContext(), "Member already exists or project not found", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } else {
                    android.widget.Toast.makeText(requireContext(), "Email cannot be empty", android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    // Shows the add task pop up
    @SuppressLint("MissingInflatedId")
    private fun showAddTaskDialog(projectID: String?) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.task_add_task, null)

        val taskNameInput = dialogView.findViewById<EditText>(R.id.editTextTaskName)
        val taskDescriptionInput = dialogView.findViewById<EditText>(R.id.editTextTaskDescription)
        val dayInput = dialogView.findViewById<EditText>(R.id.editTextDay)
        val monthInput = dialogView.findViewById<EditText>(R.id.editTextMonth)
        val yearInput = dialogView.findViewById<EditText>(R.id.editTextYear)
        val assignedToSpinner = dialogView.findViewById<Spinner>(R.id.spinnerAssignedTo)

        // Fetch project members
        val membersList = mutableListOf("Select member")
        projectID?.let {
            val project = ProjectsRepository.projects.find { p -> p.projectID == it }
            project?.members?.let { members -> membersList.addAll(members) }
        }

        val membersAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, membersList)
        membersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assignedToSpinner.adapter = membersAdapter

        builder.setView(dialogView)
            .setTitle("Add Task")
            .setPositiveButton("Add") { dialog, _ ->
                val taskName = taskNameInput.text.toString().trim()
                val taskDescription = taskDescriptionInput.text.toString().trim()
                val dayStr = dayInput.text.toString().trim()
                val monthStr = monthInput.text.toString().trim()
                val yearStr = yearInput.text.toString().trim()
                val assignedTo = assignedToSpinner.selectedItem.toString()

                if (taskName.isEmpty() || taskDescription.isEmpty() ||
                    dayStr.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty() ||
                    assignedTo == "Select member") {
                    android.widget.Toast.makeText(requireContext(), "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                try {
                    val day = dayStr.toInt()
                    val month = monthStr.toInt()
                    val year = yearStr.toInt()

                    val calendar = java.util.Calendar.getInstance()
                    calendar.isLenient = false // Ensures strict date parsing
                    calendar.set(year, month - 1, day) // month is 0-based

                    val dueDate = calendar.time

                    if (projectID != null) {
                        val newTask = Task(
                            taskID = UUID.randomUUID().toString(),
                            name = taskName,
                            description = taskDescription,
                            dueDate = dueDate,
                            assignedTo = assignedTo,
                            colorStatus = "#B0B0B0".toColorInt(),
                            status = "In Que To Start",
                            projectID = projectID
                        )

                        TaskRepository.tasks.add(newTask)

                        val updatedList = TaskRepository.tasks
                            .filter { it.projectID == projectID }
                            .sortedBy { it.dueDate }

                        adapter.updateTasks(updatedList)

                        android.widget.Toast.makeText(requireContext(), "Task added successfully", android.widget.Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Exception) {
                    android.widget.Toast.makeText(requireContext(), "Invalid date", android.widget.Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun confirmDeleteTask(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete \"${task.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                deleteTask(task)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteTask(task: Task) {
        TaskRepository.tasks.remove(task)

        // Refresh the list
        val projectID = arguments?.getString("projectID")
        tasksList = TaskRepository.tasks
            .filter { it.projectID == projectID }
            .sortedBy { it.dueDate }

        adapter.updateTasks(tasksList)

        android.widget.Toast.makeText(requireContext(), "Task deleted", android.widget.Toast.LENGTH_SHORT).show()
    }

}