package com.epfl.esl.tidy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class ItemAdapter(val context: Context, val task_name: ArrayList<String>,
                  val task_place: ArrayList<String>, val task_date: ArrayList<String>,) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.task_row,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * This new ViewHolder should be constructed with a new View that can
    represent the items
     * of the given type. You can either create a new View manually or inflate it
    from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task_name_position = task_name.get(position)
        val task_date_position = task_date.get(position)
        val task_place_position = task_place.get(position)


        holder.task_name_text.text = task_name_position.plus(" in ").plus(task_place_position.lowercase())
        holder.task_date_text.text = task_date_position
        when (task_place_position) {
            "Kitchen" -> holder.task_place_image.setImageResource(R.drawable.chef)
            "Living Room" -> holder.task_place_image.setImageResource(R.drawable.sofa)
            "Room" -> holder.task_place_image.setImageResource(R.drawable.bed)
            "Toilet" -> holder.task_place_image.setImageResource(R.drawable.wc)
            else -> { // Note the block
                holder.task_place_image.setImageResource(R.drawable.unknown)
            }
        }

    }
    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return task_name.size}

    /**
     * A ViewHolder describes an item view and metadata about
    its place within the RecyclerView.
     */
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        // Holds the TextView that will add each item to
        var task_name_text : TextView
        var task_date_text : TextView
        var task_place_image : ImageView

        init {
            task_name_text = view.findViewById<TextView>(R.id.taskName)
            task_date_text = view.findViewById<TextView>(R.id.taskDate)
            task_place_image = view.findViewById<ImageView>(R.id.taskPlace)

//            view.setOnClickListener(this)
        }

//        override fun onClick(p0: View?){
//            val position : Int = bindingAdapterPosition
//            if(position != RecyclerView.NO_POSITION) {
//                listener.onItemClick(position)
//            }
//        }
    }
    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    }

