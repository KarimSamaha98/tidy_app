package com.epfl.esl.tidy.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.R

class TasksAdapter (val context: Context?, val task_names: ArrayList<String>,
                    val due_dates: ArrayList<String>, val users: ArrayList<String>) :
    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    fun deleteItem(i : Int){
        task_names.removeAt(i)
        due_dates.removeAt(i)
        users.removeAt(i)

        notifyDataSetChanged()
    }


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

        val itemPosition1 = task_names.get(position)
        val itemPosition2 = due_dates.get(position)
        val itemPosition3 = users.get(position)

        holder.taskNames.text = itemPosition1
        holder.taskDueDates.text = itemPosition2
        holder.taskUsers.text = itemPosition3
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return task_names.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        var taskNames: TextView = view.findViewById<TextView>(R.id.task_name)
        var taskDueDates: TextView = view.findViewById<TextView>(R.id.task_due_date)
        var taskUsers: TextView = view.findViewById<TextView>(R.id.task_user)
    }

}