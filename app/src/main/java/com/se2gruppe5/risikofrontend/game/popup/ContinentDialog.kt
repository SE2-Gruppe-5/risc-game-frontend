package com.se2gruppe5.risikofrontend.game.popup

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.dataclasses.ContinentInfoRecord


@SuppressWarnings("unused")
class ContinentDialog : DialogFragment() {

    val continentList = listOf(
        ContinentInfoRecord("Power Supply", "#b5edf9", 6, 2),
        ContinentInfoRecord("MMC", "#e8f3a8", 2, 2),
        ContinentInfoRecord("RAM", "#87deb3", 10, 2),
        ContinentInfoRecord("DCON", "#f0a8e1", 5, 2),
        ContinentInfoRecord("CPU", "#e6afaf", 6, 2),
        ContinentInfoRecord("Essentials", "#fec466", 7, 2),
        ContinentInfoRecord("Southbridge", "#f5d557", 4, 2),
        ContinentInfoRecord("Wireless Mesh", "#9effad", 8, 2),
        ContinentInfoRecord("Embedded Controller", "#9b3e3e", 7, 2),
        ContinentInfoRecord("CMOS", "#a4e5e2", 5, 2)
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

