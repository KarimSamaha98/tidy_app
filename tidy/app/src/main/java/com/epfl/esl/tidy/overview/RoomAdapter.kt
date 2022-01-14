package com.epfl.esl.tidy.overview

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.admin.Room
import com.squareup.picasso.Picasso

class RoomAdapter(val context: Context?,
                  val items: List<Room?>,) :
    RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    /**
     * Inflates the item views which is designed in xml layout file
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.room_recycle,
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

        val item_position : Room? = items.get(position)

        holder.tvItem.text = item_position?.room
        Picasso.with(context)
            .load(item_position?.imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .fit()
            .centerCrop()
            .into(holder.tvItem_2)
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        var tvItem = view.findViewById<TextView>(R.id.room_name)
        var tvItem_2 = view.findViewById<ImageView>(R.id.room_photo)
    }
}