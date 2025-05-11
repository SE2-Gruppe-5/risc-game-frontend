package com.se2gruppe5.risikofrontend.game.popup

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.ContinentInfoRecord
import androidx.core.graphics.toColorInt

class ContinentListAdapter(
    context: Context,
    private val data: List<ContinentInfoRecord>
) : ArrayAdapter<ContinentInfoRecord>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val title = itemView.findViewById<TextView>(android.R.id.text1)
        val subtitle = itemView.findViewById<TextView>(android.R.id.text2)

        val item = data[position]
        title.text = item.name
        subtitle.text = buildString {
            append(item.regions)
            append(" Gebiete – +")
            append(item.bonus)
            append(" Truppen")
        }
        title.setTextColor(item.colorHex.toColorInt())

        Log.d("ContinentListAdapter", "Displaying: ${item.name} - ${item.regions} Gebiete, +${item.bonus} Truppen")

        return itemView
    }
}
