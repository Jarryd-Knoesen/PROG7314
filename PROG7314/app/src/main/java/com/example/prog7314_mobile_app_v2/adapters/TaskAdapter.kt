package com.example.prog7314_mobile_app_v2.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
//import com.example.prog7314_mobile_app_v2.EditTask
import com.example.prog7314_mobile_app_v2.R
import com.example.prog7314_mobile_app_v2.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter (private val tasks: List<Task>, private val context: Context) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskColor: CardView = itemView.findViewById(R.id.taskColor)
        val taskArrow: TextView = itemView.findViewById(R.id.taskArrow)
        val taskDueDate: TextView = itemView.findViewById(R.id.taskDueDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.name
        holder.taskDueDate.text = "Due: ${dateFormat.format(task.dueDate)}"
//        holder.taskDueDate.text = dateFormat.format(task.dueDate)
        holder.taskColor.setCardBackgroundColor(task.color)
        holder.taskArrow.text = "→"

        // Set click listener for the task item
        holder.itemView.setOnClickListener {
            showTaskDialog(task)
        }
    }

    override fun getItemCount(): Int = tasks.size

    private fun showTaskDialog(task: Task) {
        // Determines status based on the color
        val status = when (task.color) {
            Color.parseColor("#28A745") -> "Complete" // Green
            Color.parseColor("#007BFF") -> "In Progress" // Blue
            Color.parseColor("#DC3545") -> "Issue" // Red
            Color.parseColor("#B0B0B0") -> "To Do" // Gray
            else -> "Unknown"
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle(task.name)
        builder.setMessage(
            "Description: ${task.description}\n\n" +
                    "Due: ${dateFormat.format(task.dueDate)}\n\n" +
                    "Status: $status\n\n" +
                    "Task ID: ${task.taskID}"
        )
        builder.setPositiveButton("Edit") { dialog, _ ->

            dialog.dismiss()
        }


        builder.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

}