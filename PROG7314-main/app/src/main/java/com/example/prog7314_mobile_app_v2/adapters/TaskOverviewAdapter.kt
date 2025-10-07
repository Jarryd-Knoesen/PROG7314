package com.example.prog7314_mobile_app_v2.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.R
import com.example.prog7314_mobile_app_v2.models.TaskModel
import java.text.SimpleDateFormat
import java.util.Locale

class TaskOverviewAdapter(private var tasks: List<TaskModel>) :
    RecyclerView.Adapter<TaskOverviewAdapter.TaskViewHolder>() {

    fun updateList(newList: List<TaskModel>) {
        tasks = newList
        notifyDataSetChanged()
    }

    fun updateSearchList(newList: List<TaskModel>) {
        tasks = newList
        notifyDataSetChanged()
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskStatus: TextView = itemView.findViewById(R.id.taskStatus)
        val taskDueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        val taskAssigned: TextView = itemView.findViewById(R.id.taskAssigned)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        val taskDocs: TextView = itemView.findViewById(R.id.taskDocs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_overview_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.name
        holder.taskDueDate.text = task.dueDate?.let { dateFormat.format(it) } ?: "No Due Date"
        holder.taskAssigned.text = task.assignedTo
        holder.taskDescription.text = task.description ?: ""
        holder.taskDocs.text = "📄"

        holder.taskStatus.text = when (safeParseColor(task.colorStatus)) {
            Color.parseColor("#28A745") -> "Completed"
            Color.parseColor("#007BFF") -> "In Progress"
            Color.parseColor("#DC3545") -> "Issue / Stuck"
            else -> "In Que To Do"
        }
    }

    /** Safely parse color strings, return a default if invalid */
    private fun safeParseColor(colorString: String?): Int {
        return try {
            if (!colorString.isNullOrBlank()) {
                Color.parseColor(colorString)
            } else {
                Color.BLACK // default color
            }
        } catch (e: IllegalArgumentException) {
            Color.BLACK // fallback
        }
    }


    override fun getItemCount(): Int = tasks.size
}
