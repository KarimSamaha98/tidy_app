package com.epfl.esl.tidy.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.R

class TasksAdapter (val context: Context, val task_name: ArrayList<String>,
                    val due_date: ArrayList<String>, val user: ArrayList<String>) :
    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {
    /**
     * Inflates the item views which is designed in xml layout file
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.tasks_recycle,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemPosition1 = task_name.get(position)
        val itemPosition2 = due_date.get(position)
        val itemPosition3 = user.get(position)

        holder.taskName.text = itemPosition1
        holder.taskDueDate.text = itemPosition2
        holder.taskUser.text = itemPosition3
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return task_name.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        var taskName: TextView = view.findViewById<TextView>(R.id.task_name)
        var taskDueDate: TextView = view.findViewById<TextView>(R.id.task_due_date)
        var taskUser: TextView = view.findViewById<TextView>(R.id.task_user)
    }
}