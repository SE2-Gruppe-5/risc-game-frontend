package com.se2gruppe5.risikofrontend.troopcount

import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TroopService(private val client: HttpClient){
    suspend fun fetchTerritory(territoryId: Int): TerritoryRecord {
        return client.get("http://your-backend-api.com/api/territories/$territoryId").body()
    }

    suspend fun updateTerritory(territory: TerritoryRecord) {
        client.post("http://your-backend-api.com/api/territories/update") {
            contentType(ContentType.Application.Json)
            setBody(territory)
        }
    }
}