package com.se2gruppe5.risikofrontend.popup

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.se2gruppe5.risikofrontend.R


@SuppressWarnings("unused")
class ContinentDialog : DialogFragment(){

    val continentList = listOf(
        ContinentInfo("Power Supply", "#b5edf9", 6, 2),
        ContinentInfo("MMC", "#e8f3a8", 2, 2),
        ContinentInfo("RAM", "#87deb3", 10, 2),
        ContinentInfo("DCON", "#f0a8e1", 5, 2),
        ContinentInfo("CPU", "#e6afaf", 6, 2),
        ContinentInfo("Essentials", "#fec466", 7, 2),
        ContinentInfo("Southbridge", "#f5d557", 4, 2),
        ContinentInfo("Wireless Mesh", "#9effad", 8, 2),
        ContinentInfo("Embedded Controller", "#9b3e3e", 7, 2),
        ContinentInfo("CMOS", "#a4e5e2", 5, 2)
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

