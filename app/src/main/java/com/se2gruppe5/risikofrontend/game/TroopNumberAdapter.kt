package com.se2gruppe5.risikofrontend.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.se2gruppe5.risikofrontend.R

class TroopNumberAdapter : RecyclerView.Adapter<TroopNumberAdapter.NumberViewHolder>() {

    var itemWidth: Int = 0
    var numbers: List<Int> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.troop_selector_number, parent, false)
        return NumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.text.text = numbers[position].toString()
        if (itemWidth > 0) {
            holder.itemView.layoutParams.width = itemWidth
            holder.itemView.requestLayout()
        }
    }

    override fun getItemCount(): Int = numbers.size

    class NumberViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {
        val text: android.widget.TextView =
            itemView.findViewById(R.id.tvNumber)
    }
}
