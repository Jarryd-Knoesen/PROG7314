package com.example.prog7314_mobile_app_v2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7314_mobile_app_v2.R
import com.example.prog7314_mobile_app_v2.models.Projects

class ProjectAdapter(
    private var projects: List<Projects>,
    private val onProjectClick: (Projects) -> Unit,
    private val onDeleteClick: (Projects) -> Unit // new callback
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectName: TextView = itemView.findViewById(R.id.projectName)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteProject) // new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_project_card_item, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.projectName.text = project.name

        holder.itemView.setOnClickListener {
            onProjectClick(project)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(project)
        }
    }

    override fun getItemCount(): Int = projects.size

    fun updateProjects(newProjects: List<Projects>) {
        projects = newProjects
        notifyDataSetChanged()
    }
}
