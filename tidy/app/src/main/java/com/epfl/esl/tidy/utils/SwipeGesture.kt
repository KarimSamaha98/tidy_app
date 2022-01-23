package com.epfl.esl.tidy.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.epfl.esl.tidy.tasks.TasksAdapter
import com.epfl.esl.tidy.tasks.TasksFragment

abstract class SwipeGesture : ItemTouchHelper.Callback(){
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val position = viewHolder.adapterPosition
        val tasksList = TasksFragment.tasksList
        return if (tasksList[position].rank == 1){
            val swipeFlag = ItemTouchHelper.RIGHT
            makeMovementFlags(0, swipeFlag)
        }else{
            val swipeFlag = 0
            makeMovementFlags(0, swipeFlag)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}