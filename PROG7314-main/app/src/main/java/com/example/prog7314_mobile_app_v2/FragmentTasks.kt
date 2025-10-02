package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.app.AlertDialog
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

        taskNameText.text = task.name
        taskDescriptionText.text = task.description

        taskDueDateText.text = getString(
            R.string.task_dialog_due_text,
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(task.dueDate)
        )

        taskAssignedToText.text = getString(
            R.string.task_dialog_assigned_to_text,
            task.assignedTo
        )

        taskStatus.text = getString(
            R.string.task_dialog_status_text,
            task.status
        )

        builder.setView(dialogView)
            .setNegativeButton(getString(R.string.task_dialog_edit_button)) { _, _ ->
                val fragmentTaskEdit = FragmentTaskEdit()
                val bundle = Bundle()
                bundle.putSerializable("task", task)
                fragmentTaskEdit.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentTaskEdit)
                    .addToBackStack(null)
                    .commit()
            }
            .setNeutralButton(getString(R.string.task_dialog_delete_button)) { _, _ ->
                confirmDeleteTask(task)
            }
            .setPositiveButton(getString(R.string.task_dialog_close_button)) { dialog, _ ->
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
            .setTitle(getString(R.string.add_member_title))
            .setPositiveButton(getString(R.string.add_member_button)) { dialog, _ ->
                val email = emailInput.text.toString().trim()

                if (email.isNotEmpty() && projectID != null) {
                    val success = ProjectsRepository.addMemberToProject(projectID, email)
                    val message = if (success) {
                        getString(R.string.add_member_user_added_alert, email)
                    } else {
                        getString(R.string.add_member_issue_alert)
                    }
                    android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(requireContext(), getString(R.string.add_member_email_empty_alert), android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.add_member_cancel_button)) { dialog, _ ->
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
        val membersList = mutableListOf(getString(R.string.add_task_select_member_text))
        projectID?.let {
            val project = ProjectsRepository.projects.find { p -> p.projectID == it }
            project?.members?.let { members -> membersList.addAll(members) }
        }

        val membersAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, membersList)
        membersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assignedToSpinner.adapter = membersAdapter

        builder.setView(dialogView)
            .setTitle(getString(R.string.add_task_title))
            .setPositiveButton(getString(R.string.add_task_button)) { dialog, _ ->
                val taskName = taskNameInput.text.toString().trim()
                val taskDescription = taskDescriptionInput.text.toString().trim()
                val dayStr = dayInput.text.toString().trim()
                val monthStr = monthInput.text.toString().trim()
                val yearStr = yearInput.text.toString().trim()
                val assignedTo = assignedToSpinner.selectedItem.toString()

                if (taskName.isEmpty() || taskDescription.isEmpty() ||
                    dayStr.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty() ||
                    assignedTo == getString(R.string.add_task_select_member_text)) {
                    android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_fields_error), android.widget.Toast.LENGTH_SHORT).show()
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
                            status = getString(R.string.add_task_status_set),
                            projectID = projectID
                        )

                        TaskRepository.tasks.add(newTask)

                        val updatedList = TaskRepository.tasks
                            .filter { it.projectID == projectID }
                            .sortedBy { it.dueDate }

                        adapter.updateTasks(updatedList)

                        android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_successful), android.widget.Toast.LENGTH_SHORT).show()
                    }
                } catch (_: Exception) {
                    android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_error_date), android.widget.Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.add_task_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun confirmDeleteTask(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_task_title))
            .setMessage(getString(R.string.delete_task_confirmation, task.name))
            .setPositiveButton(getString(R.string.delete_task_delete_button)) { _, _ ->
                deleteTask(task)
            }
            .setNegativeButton(getString(R.string.delete_task_cancel_button)) { dialog, _ ->
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

        android.widget.Toast.makeText(requireContext(), getString(R.string.delete_task_task_deleted_confirmation), android.widget.Toast.LENGTH_SHORT).show()
    }

}