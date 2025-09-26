package com.example.prog7314_mobile_app_v2.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.R
import com.example.prog7314_mobile_app_v2.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskOverviewAdapter(private val tasks: List<Task>) :
    RecyclerView.Adapter<TaskOverviewAdapter.TaskViewHolder>() {

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
        holder.taskDueDate.text = dateFormat.format(task.dueDate)
        holder.taskAssigned.text = task.assignedTo
        holder.taskDescription.text = task.description
        holder.taskDocs.text = "📄"

        holder.taskStatus.text = when (task.color) {
            Color.parseColor("#28A745") -> "Completed"
            Color.parseColor("#007BFF") -> "Doing"
            Color.parseColor("#DC3545") -> "Issue"
            else -> "To Do"
        }
    }

    override fun getItemCount(): Int = tasks.size
}
