package com.se2gruppe5.risikofrontend.Popup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.se2gruppe5.risikofrontend.R

data class ContinentInfo(
    val name: String,
    val colorHex: String,
    val regions: Int,
    val bonus: Int
)
@SuppressWarnings("unused")
class ContinentDialog : DialogFragment(){

    @SuppressWarnings("DialogUsage")
    private val continentList = listOf(
        ContinentInfo("Europa", "#4CAF50", 6, 5),
        ContinentInfo("Asien", "#E91E63", 7, 7),
        ContinentInfo("Afrika", "#F44336", 6, 3),
        ContinentInfo("Südamerika", "#FFEB3B", 4, 2),
        ContinentInfo("Australien", "#FF9800", 3, 2),
        ContinentInfo("Nordamerika", "#03A9F4", 5, 4)
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_continents, null)

        val listView = view.findViewById<ListView>(R.id.continentListView)
        val adapter = ContinentListAdapter(requireContext(), continentList)
        listView.adapter = adapter

        builder.setView(view)
            .setTitle("Kontinente & Bonus")
            .setPositiveButton("Schließen") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}

class ContinentListAdapter(
    context: Context,
    private val data: List<ContinentInfo>
) : ArrayAdapter<ContinentInfo>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val title = itemView.findViewById<TextView>(android.R.id.text1)
        val subtitle = itemView.findViewById<TextView>(android.R.id.text2)

        val item = data[position]
        title.text = item.name
        subtitle.text = "${item.regions} Gebiete – +${item.bonus} Truppen"
        title.setTextColor(Color.parseColor(item.colorHex))

        Log.d("ContinentListAdapter", "Displaying: ${item.name} - ${item.regions} Gebiete, +${item.bonus} Truppen")


        return itemView
    }
}