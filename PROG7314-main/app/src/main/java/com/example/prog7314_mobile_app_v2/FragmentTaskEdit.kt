package com.example.prog7314_mobile_app_v2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.prog7314_mobile_app_v2.Retrofit.ApiClient
import com.example.prog7314_mobile_app_v2.Retrofit.TasksApi
import com.example.prog7314_mobile_app_v2.models.ProjectsRepository
import com.example.prog7314_mobile_app_v2.models.Task
import com.example.prog7314_mobile_app_v2.models.UpdateTaskModel
import java.util.Calendar

class FragmentTaskEdit : Fragment() {

    private val TAG = "FragmentTaskEdit"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_edit, container, false)

        val task = arguments?.getSerializable("task") as? Task
        val projectId = arguments?.getString("projectId") ?: ""

        Log.e(TAG, "Loaded task: $task")
        Log.e(TAG, "Project ID: $projectId")

        val projectName = ProjectsRepository.projects
            .firstOrNull { it.projectID == projectId }
            ?.name ?: "Unknown Project"

        view.findViewById<TextView>(R.id.txtProjectName).text =
            getString(R.string.task_project_name, projectName)

        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            Log.e(TAG, "Save button clicked")
            updateTask(task!!, projectId)
        }

        task?.let {
            val projectName = ProjectsRepository.projects
                .firstOrNull { project -> project.projectID == it.projectID }
                ?.name ?: "Unknown Project"

            view.findViewById<TextView>(R.id.txtProjectName).text =
                getString(R.string.task_project_name, projectName)
            view.findViewById<TextView>(R.id.txtTaskName).text = it.name
            view.findViewById<TextView>(R.id.txtDescription).text = it.description
            view.findViewById<TextView>(R.id.txtAssignedTo).text =
                getString(R.string.task_assigned_to, it.assignedTo)

            val calendar = Calendar.getInstance()
            calendar.time = it.dueDate
            view.findViewById<EditText>(R.id.editTextDay).hint =
                calendar.get(Calendar.DAY_OF_MONTH).toString()
            view.findViewById<EditText>(R.id.editTextMonth).hint =
                (calendar.get(Calendar.MONTH) + 1).toString()
            view.findViewById<EditText>(R.id.editTextYear).hint =
                calendar.get(Calendar.YEAR).toString()

            val spinner = view.findViewById<Spinner>(R.id.spinnerStatus)
            val statuses = listOf(
                getString(R.string.status_in_que_to_do),
                getString(R.string.status_doing),
                getString(R.string.status_completed),
                getString(R.string.status_issue_stuck)
            )
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val currentIndex = statuses.indexOf(it.status)
            if (currentIndex >= 0) {
                spinner.setSelection(currentIndex)
            }
        }

        return view
    }

    private fun updateTask(task: Task, projectId: String) {
        val dayText = view?.findViewById<EditText>(R.id.editTextDay)?.text.toString()
        val monthText = view?.findViewById<EditText>(R.id.editTextMonth)?.text.toString()
        val yearText = view?.findViewById<EditText>(R.id.editTextYear)?.text.toString()

        Log.e(TAG, "Day text: $dayText, Month text: $monthText, Year text: $yearText")

        val spinner = view?.findViewById<Spinner>(R.id.spinnerStatus)
        val status = spinner?.selectedItem.toString()

        Log.e(TAG, "Selected status: $status")

        val calendar = Calendar.getInstance()
        try {
            val day = if (dayText.isNotEmpty()) dayText.toInt() else task.dueDate.date
            val month = if (monthText.isNotEmpty()) monthText.toInt() - 1 else task.dueDate.month
            val year = if (yearText.isNotEmpty()) yearText.toInt() else task.dueDate.year + 1900

            Log.e(TAG, "Parsed date: $day/${month + 1}/$year")
            calendar.set(year, month, day)
        } catch (e: Exception) {
            Log.e(TAG, "Date parsing error", e)
            e.printStackTrace()
            return
        }

        val updatedDueDate = calendar.time
        Log.e(TAG, "Updated due date: $updatedDueDate")

        // Format date as ISO 8601 string
        val isoDueDate = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }
            .format(updatedDueDate)

        var colorStatusUpdate = ""

        if (status == getString(R.string.status_in_que_to_do)){
            colorStatusUpdate = "#B0B0B0"
        } else  if (status == getString(R.string.status_doing)){
            colorStatusUpdate = "#007BFF"
        } else  if (status == getString(R.string.status_completed)){
            colorStatusUpdate = "#28A745"
        } else {
            colorStatusUpdate = "#DC3545"
        }


        // Create UpdateTaskModel using existing task values
        val taskUpdate = UpdateTaskModel(
            name = task.name,
            description = task.description,
            assignedTo = task.assignedTo,
            colorStatus = colorStatusUpdate, // Ensure this matches API expectation
            status = status,
            dueDate = isoDueDate
        )

        Log.e(TAG, "Updating task with ID: ${task.taskID}")
        Log.e(TAG, "Update payload: $taskUpdate")

        val api = ApiClient.instance.create(TasksApi::class.java)
        val call = api.updateTask(projectId, task.taskID, taskUpdate)

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    Log.e(TAG, "Task update successful")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Log.e(TAG, "Task update failed: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                Log.e(TAG, "Task update failed with error", t)
                t.printStackTrace()
            }
        })
    }

}
