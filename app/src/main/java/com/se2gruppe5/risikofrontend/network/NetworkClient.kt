package com.se2gruppe5.risikofrontend.network

import com.se2gruppe5.risikofrontend.Constants
import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.util.UUID

class NetworkClient : INetworkClient {
    val client = OkHttpClient()

    override suspend fun sendChat(message: String) {
        val request = createRequest(
            "POST", Constants.CHAT_SEND_URL,
            "message", message
        )
        execute(request)
    }

    override suspend fun createLobby(): String? {
        val request = createRequest("GET", Constants.LOBBY_CREATE_URL)
        val response = execute(request)
        return if (response.isSuccessful) response.body?.string() else null
    }

    override suspend fun deleteLobby(lobbyCode: String) {
        val request =
            createRequest("DELETE", Constants.LOBBY_RESOURCE_URL.replace("{id}", lobbyCode))
        execute(request)
    }

    override suspend fun joinLobby(lobbyCode: String, playerName: String) {
        val request = createRequest(
            "PUT", Constants.LOBBY_PLAYER_URL.replace("{id}", lobbyCode),
            "uuid", SseClientService.uuid.toString(),
            "name", playerName
        )
        execute(request)
    }

    override suspend fun leaveLobby(lobbyCode: String) {
        val request = createRequest(
            "DELETE", Constants.LOBBY_PLAYER_URL.replace("{id}", lobbyCode),
            "uuid", SseClientService.uuid.toString()
        )
        execute(request)
    }

    override suspend fun startGame(lobbyCode: String) {
        val request =
            createRequest("GET", Constants.LOBBY_START_GAME_URL.replace("{id}", lobbyCode))
        execute(request)
    }

    override suspend fun updatePlayer(gameId: UUID, playerId: UUID, name: String, color: Int) {
        val request = createRequest(
            "PATCH",
            Constants.UPDATE_PLAYER_URL.replace("{id}", gameId.toString())
                .replace("{playerId}", playerId.toString()),
            "name",
            name,
            "color",
            color.toString()
        )
        execute(request)
    }

    override suspend fun getGameInfo(gameId: UUID) {
        val request = createRequest(
            "POST", Constants.GET_GAME_INFO_URL.replace("{id}", gameId.toString()),
            "uuid", SseClientService.uuid.toString()
        )
        execute(request)
    }

    override suspend fun changePhase(gameId: UUID) {
        val request =
            createRequest("GET", Constants.CHANGE_PHASE_URL.replace("{id}", gameId.toString()))
        execute(request)
    }

    override suspend fun changeTerritory(gameId: UUID, territory: TerritoryRecord) {
        val request = createRequest(
            "PATCH", Constants.CHANGE_TERRITORY_URL.replace("{id}", gameId.toString()),
            "owner", territory.owner?.toString(),
            "id", territory.id.toString(),
            "stat", territory.stat.toString()
        )
        execute(request)
    }

    override suspend fun cardAction(
        gameId: UUID,
        action: String,
        player: PlayerRecord,
        card: CardRecord
    ) {
        TODO("Not yet implemented")
    }

    private fun createRequest(method: String, path: String, vararg params: String?): Request {
        val body = if (params.isNotEmpty()) {
            MultipartBody.Builder()
                .apply {
                    for (i in params.indices step 2) {
                        if (params[i] == null || params[i + 1] == null) {
                            continue
                        }
                        addFormDataPart(params[i]!!, params[i + 1]!!)
                    }
                }
                .build()
        } else {
            null
        }
        return Request.Builder()
            .url(Constants.HOST + path)
            .method(method, body)
            .build()
    }

    private suspend fun execute(request: Request): Response {
        val call = client.newCall(request)
        return withContext(Dispatchers.IO) {
            return@withContext call.execute()
        }
    }

    override suspend fun cheatConquer(
        gameId: UUID,
        playerId: UUID,
        territoryId: Int
    ): Result<Unit> {
        return try {
            val url = "http://10.0.2.2:8080/game/$gameId/cheat/conquer" +
                    "?playerId=$playerId&territoryId=$territoryId"

            val request = Request.Builder()
                .url(url)
                .post(RequestBody.create(null, ByteArray(0))) // Leerer POST-Body
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Server error: ${response.code}"))
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
