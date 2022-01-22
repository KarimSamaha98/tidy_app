package com.epfl.esl.tidy.overview

import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.epfl.esl.tidy.R
import com.epfl.esl.tidy.admin.Room
import com.squareup.picasso.Picasso

class OverviewAdapter(val context: Context?,) : RecyclerView.Adapter<OverviewAdapter.OverviewAdapterViewHolder>() {

    private val adapterOverview = mutableListOf<DataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewAdapterViewHolder {
        val layout = when (viewType) {
            TYPE_ROOM -> R.layout.room_recycle
            TYPE_SUPPLY-> R.layout.supply_recycle
            TYPE_TITLE -> R.layout.title_recycle
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return OverviewAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: OverviewAdapterViewHolder, position: Int) {
        holder.bind(adapterOverview[position])
    }

    override fun getItemCount(): Int {
        return adapterOverview.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(adapterOverview[position]) {
            is DataModel.Room -> TYPE_ROOM
            is DataModel.Supply -> TYPE_SUPPLY
            is DataModel.Header -> TYPE_TITLE
        }
    }

    fun setData(data: List<DataModel>) {
        adapterOverview.apply {
            clear()
            addAll(data)
        }
    }

    companion object {
        private const val TYPE_ROOM = 0
        private const val TYPE_SUPPLY = 1
        private const val TYPE_TITLE = 2
    }

    inner class OverviewAdapterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private fun bindRoom(item : DataModel.Room) {
            itemView.findViewById<TextView>(R.id.room_name).text = item.room
            Picasso.with(context)
                .load(item.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(itemView.findViewById<ImageView>(R.id.room_photo))
        }
        private fun bindSupply(item : DataModel.Supply) {
            itemView.findViewById<TextView>(R.id.supply_name).text = item.name
            Picasso.with(context)
                .load(item.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(itemView.findViewById<ImageView>(R.id.supply_photo))
        }
        private fun bindHeader(item: DataModel.Header){
            val layoutParams = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = true
            itemView.findViewById<TextView>(R.id.title_recycle).text = item.title
        }
        fun bind(dataModel : DataModel) {
            when(dataModel) {
                is DataModel.Room -> bindRoom(dataModel)
                is DataModel.Supply -> bindSupply(dataModel)
                is DataModel.Header -> bindHeader(dataModel)
            }
        }
    }
}