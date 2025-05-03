package com.se2gruppe5.risikofrontend.TroopCount

import android.content.Context
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.call.body
import io.ktor.client.request.setBody

class TroopCountManager(private val context: Context) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun fetchTroopsForTerritory(territoryId: Int, textView: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val territory: TerritoryRecord =
                    client.get("http://your-backend-api.com/api/territories/$territoryId").body()

                withContext(Dispatchers.Main) {
                    // Aktualisiere die Truppenanzeige im UI
                    textView.text = "Troops: ${territory.stat}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    textView.text = "Error fetching troops"
                }
            }
        }
    }

    fun updateTroops(territoryId: Int, newTroopCount: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val territory = TerritoryRecord(territoryId, newTroopCount)
                client.post("http://your-backend-api.com/api/territories/update") {
                    contentType(ContentType.Application.Json)
                    setBody(territory)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
