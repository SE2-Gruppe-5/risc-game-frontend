package com.se2gruppe5.risikofrontend.game.popup

import android.R
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.ContinentInfoRecord

class ContinentListAdapter(
    context: Context,
    private val data: List<ContinentInfoRecord>
) : ArrayAdapter<ContinentInfoRecord>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.simple_list_item_2, parent, false)

        val title = itemView.findViewById<TextView>(R.id.text1)
        val subtitle = itemView.findViewById<TextView>(R.id.text2)

        val item = data[position]
        title.text = item.name
        subtitle.text = "${item.regions} Gebiete – +${item.bonus} Truppen"
        title.setTextColor(Color.parseColor(item.colorHex))

        Log.d("ContinentListAdapter", "Displaying: ${item.name} - ${item.regions} Gebiete, +${item.bonus} Truppen")


        return itemView
    }
}