package com.se2gruppe5.risikofrontend.game.popup

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.enums.Continent


class ContinentDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_continents, null)

        val listView = view.findViewById<ListView>(R.id.continentListView)
        val adapter = ContinentListAdapter(requireContext(), Continent.entries.toList())
        listView.adapter = adapter

        builder.setView(view)
            .setTitle("Kontinente & Bonus")
            .setPositiveButton("Schließen") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}

