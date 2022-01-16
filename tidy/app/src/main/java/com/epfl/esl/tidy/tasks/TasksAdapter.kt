package com.epfl.esl.tidy.tasks

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.R

class TasksAdapter (val context: Context?, var tasks : ArrayList<TasksAdapterClass>) :
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
        val itemPosition = tasks.get(position)
        // change color
        if (itemPosition.rank == 1){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#90CAF9"))
        }
        else{
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E3F2FD"))
        }

        holder.taskNames.text = itemPosition.task_name
        holder.taskDueDates.text = "DUE DATE: ".plus(itemPosition.due_date)
        holder.taskUsers.text = itemPosition.user
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return tasks.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        var taskNames: TextView = view.findViewById<TextView>(R.id.task_name)
        var taskDueDates: TextView = view.findViewById<TextView>(R.id.task_due_date)
        var taskUsers: TextView = view.findViewById<TextView>(R.id.task_user)
        // holds cardView ref
        val cardView : CardView = view.findViewById(R.id.tasks_view_item)
    }
}