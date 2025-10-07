package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.ProjectsApi
import com.example.prog7314_mobile_app_v2.Retrofit.TasksApi
import com.example.prog7314_mobile_app_v2.models.AssignUserModel
import com.example.prog7314_mobile_app_v2.models.CreateTaskModel
import com.example.prog7314_mobile_app_v2.models.ProjectModel
import com.example.prog7314_mobile_app_v2.models.ProjectsRepository
import com.example.prog7314_mobile_app_v2.models.TaskModel
import retrofit2.Retrofit
import java.util.Date
import java.util.TimeZone

class FragmentTasks : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var tasksList: List<Task>

    private lateinit var tasksApi: TasksApi


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

        // Retrofit
        tasksApi = ApiClient.instance.create(TasksApi::class.java)

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
            fragmentOverview.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentOverview)
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<TextView>(R.id.txtProjectName).text = projectName

        recyclerView = view.findViewById(R.id.taskTaskCardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        tasksList = TaskRepository.tasks
//            .filter { it.projectID == projectID }
//            .sortedBy { it.dueDate }

        adapter = TaskAdapter(emptyList()) { task ->
            showTaskDialog(task)
        }
        recyclerView.adapter = adapter

        projectID?.let {
            loadTasks(it)
        }


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
                bundle.putString("projectId", task.projectID)
                bundle.putString("projectName", task.name)
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
        Log.d("AddMemberDialog", "Opening add member dialog for projectID: $projectID")

        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.task_add_member, null)

        val emailInput = dialogView.findViewById<EditText>(R.id.etEmail)

        builder.setView(dialogView)
            .setTitle(getString(R.string.add_member_title))
            .setPositiveButton(getString(R.string.add_member_button)) { dialog, _ ->
                val email = emailInput.text.toString().trim()
                Log.d("AddMemberDialog", "User input email: $email")

                if (email.isNotEmpty() && projectID != null) {
                    Log.d("AddMemberDialog", "Attempting to assign user to project.")
                    val api = ApiClient.instance.create(ProjectsApi::class.java)
                    val request = AssignUserModel(email)

                    api.assignUserToProject(projectID, request)
                        .enqueue(object : retrofit2.Callback<Void> {
                            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("AddMemberDialog", "User added successfully: $email")
                                    Toast.makeText(requireContext(), "User $email added successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e("AddMemberDialog", "Failed to add user. Response code: ${response.code()}")
                                    Toast.makeText(requireContext(), "Error adding user", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                                Log.e("AddMemberDialog", "API call failed", t)
                                Toast.makeText(requireContext(), "Error adding user", Toast.LENGTH_SHORT).show()
                            }
                        })

                } else {
                    Log.d("AddMemberDialog", "Email is empty or projectID is null.")
                    android.widget.Toast.makeText(requireContext(), getString(R.string.add_member_email_empty_alert), android.widget.Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.add_member_cancel_button)) { dialog, _ ->
                Log.d("AddMemberDialog", "Add member dialog canceled.")
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
        Log.d("AddMemberDialog", "Add member dialog shown.")
    }




    // Shows the add task pop up
    @SuppressLint("MissingInflatedId")
    private fun showAddTaskDialog(projectID: String?) {
        Log.d("AddTaskDialog", "Opening add task dialog for projectID: $projectID")

        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.task_add_task, null)

        val taskNameInput = dialogView.findViewById<EditText>(R.id.editTextTaskName)
        val taskDescriptionInput = dialogView.findViewById<EditText>(R.id.editTextTaskDescription)
        val dayInput = dialogView.findViewById<EditText>(R.id.editTextDay)
        val monthInput = dialogView.findViewById<EditText>(R.id.editTextMonth)
        val yearInput = dialogView.findViewById<EditText>(R.id.editTextYear)
        val assignedToSpinner = dialogView.findViewById<Spinner>(R.id.spinnerAssignedTo)

        // Spinner setup with placeholder
        val membersList = mutableListOf(getString(R.string.add_task_select_member_text))
        val membersAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, membersList)
        membersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assignedToSpinner.adapter = membersAdapter

        // Load members dynamically from API
        projectID?.let {
            val api = ApiClient.instance.create(ProjectsApi::class.java)
            api.getProjectById(it).enqueue(object : retrofit2.Callback<ProjectModel> {
                override fun onResponse(call: retrofit2.Call<ProjectModel>, response: retrofit2.Response<ProjectModel>) {
                    if (response.isSuccessful) {
                        val project = response.body()
                        Log.d("AddTaskDialog", "Loaded project members: ${project?.memberUids}")
                        project?.memberUids?.let { members ->
                            membersList.addAll(members)
                            membersAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.e("AddTaskDialog", "Failed to load members. Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProjectModel>, t: Throwable) {
                    Log.e("AddTaskDialog", "Error loading project members", t)
                }
            })
        }

        builder.setView(dialogView)
            .setTitle(getString(R.string.add_task_title))
            .setPositiveButton(getString(R.string.add_task_button)) { dialog, _ ->
                val taskName = taskNameInput.text.toString().trim()
                val taskDescription = taskDescriptionInput.text.toString().trim()
                val dayStr = dayInput.text.toString().trim()
                val monthStr = monthInput.text.toString().trim()
                val yearStr = yearInput.text.toString().trim()
                val assignedTo = assignedToSpinner.selectedItem.toString()

                Log.d("AddTaskDialog", "Task input - Name: $taskName, Description: $taskDescription, Day: $dayStr, Month: $monthStr, Year: $yearStr, AssignedTo: $assignedTo")

                if (taskName.isEmpty() || taskDescription.isEmpty() ||
                    dayStr.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty() ||
                    assignedTo == getString(R.string.add_task_select_member_text)) {
                    Log.d("AddTaskDialog", "Validation failed - required fields missing.")
                    android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_fields_error), android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                try {
                    val day = dayStr.toInt()
                    val month = monthStr.toInt()
                    val year = yearStr.toInt()

                    val calendar = java.util.Calendar.getInstance()
                    calendar.isLenient = false
                    calendar.set(year, month - 1, day)

                    val dueDate = calendar.time
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val dueDateString = dateFormat.format(dueDate)
                    Log.d("AddTaskDialog", "Parsed due date: $dueDateString")

                    if (projectID != null) {
                        val api = ApiClient.instance.create(TasksApi::class.java)

                        val newTask = CreateTaskModel(
                            name = taskName,
                            description = taskDescription,
                            dueDate = dueDateString,
                            assignedTo = assignedTo,
                            colorStatus = "#B0B0B0",
                            status = getString(R.string.add_task_status_set)
                        )

                        Log.d("AddTaskDialog", "Creating task: $newTask")
                        api.createTask(projectID, newTask)
                            .enqueue(object : retrofit2.Callback<TaskModel> {
                                override fun onResponse(call: retrofit2.Call<TaskModel>, response: retrofit2.Response<TaskModel>) {
                                    Log.d("AddTaskDialog", "API Response: ${response.code()}")
                                    if (response.isSuccessful) {
                                        Log.d("AddTaskDialog", "Task created successfully.")
                                        android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_successful), android.widget.Toast.LENGTH_SHORT).show()
                                        loadTasks(projectID)
                                    } else {
                                        Log.e("AddTaskDialog", "Failed to create task. Response code: ${response.code()}")
                                        android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_error_date), android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: retrofit2.Call<TaskModel>, t: Throwable) {
                                    Log.e("AddTaskDialog", "API call failed", t)
                                    android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_error_date), android.widget.Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                } catch (e: Exception) {
                    Log.e("AddTaskDialog", "Exception parsing date or creating task", e)
                    android.widget.Toast.makeText(requireContext(), getString(R.string.add_task_error_date), android.widget.Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.add_task_cancel_button)) { dialog, _ ->
                Log.d("AddTaskDialog", "Add task dialog canceled.")
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
        Log.d("AddTaskDialog", "Add task dialog shown.")
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

    private fun loadTasks(projectID: String) {
        tasksApi.getTasksByProject(projectID).enqueue(object : retrofit2.Callback<List<TaskModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<TaskModel>>,
                response: retrofit2.Response<List<TaskModel>>
            ) {
                if (response.isSuccessful) {
                    val taskModels = response.body() ?: emptyList()
                    val tasks = taskModels.map { model ->
                        Task(
                            taskID = model.id ?: UUID.randomUUID().toString(),
                            name = model.name,
                            description = model.description ?: "",
                            dueDate = model.dueDate ?: Date(),
                            assignedTo = model.assignedTo,
                            colorStatus = model.colorStatus.toColorInt(),
                            status = model.status,
                            projectID = projectID
                        )
                    }

                    adapter.updateTasks(tasks)
                } else {
                    android.widget.Toast.makeText(requireContext(), "Failed to load tasks", android.widget.Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<TaskModel>>, t: Throwable) {
                android.widget.Toast.makeText(requireContext(), "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                Log.e("API_FAILURE", "Error loading tasks: ${t.message}")
            }
        })
    }

}