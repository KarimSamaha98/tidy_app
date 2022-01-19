package com.epfl.esl.tidy.overview

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.admin.Room
import com.squareup.picasso.Picasso

class RoomAdapter(val context: Context?,
                  val items: List<Room?>,
                  val delete: Boolean,
                  private val listener : OnItemClickListener) :
    RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    var clicked = -1
    /**
     * Inflates the item views which is designed in xml layout file
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(delete) {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.room_delete_recycle,
                    parent,
                    false
                )
            )
        } else {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.room_recycle,
                    parent,
                    false
                )
            )
        }
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
//        holder.cardView.setOnClickListener{
//            val previous_item = clicked
//            clicked = position
//            notifyItemChanged(previous_item)
//            notifyItemChanged(clicked)
//        }
//        if(position==clicked){
//            holder.cardView.setCardBackgroundColor(getColor(context!!, R.color.teal_700))
//        }
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
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        // Holds the TextView that will add each item to
        var tvItem = view.findViewById<TextView>(R.id.room_name)
        var tvItem_2 = view.findViewById<ImageView>(R.id.room_photo)
        val cardView : CardView = view.findViewById(R.id.room_item)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position : Int = bindingAdapterPosition
//            Make sure position is not invalid, for example if deleting an element during animation.
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}