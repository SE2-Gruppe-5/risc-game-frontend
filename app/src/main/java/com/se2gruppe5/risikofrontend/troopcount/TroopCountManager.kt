package com.se2gruppe5.risikofrontend.troopcount

import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TroopCountManager(private val troopService: TroopService) {

    // Diese Methode ruft die Truppenanzahl ab und zeigt sie im TextView an
    suspend fun fetchAndDisplayTroops(territoryId: Int, textView: TextView) {
        try {
            // Truppenanzahl vom Backend abrufen
            val territory = troopService.fetchTerritory(territoryId)
            // Auf dem Hauptthread den Text des TextViews aktualisieren
            withContext(Dispatchers.Main) {
                textView.text = "Troops: ${territory.stat}"
            }
        } catch (e: Exception) {
            // Fehlerfall: Text auf Fehler setzen
            withContext(Dispatchers.Main) {
                textView.text = "Error fetching troops"
            }
        }
    }

    suspend fun updateTroops(territoryId: Int, newTroopCount: Int): Result<Unit> {
        return try {
            troopService.updateTerritory(TerritoryRecord(territoryId, newTroopCount))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

