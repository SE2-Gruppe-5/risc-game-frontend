package com.se2gruppe5.risikofrontend.network

import com.se2gruppe5.risikofrontend.Constants
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class NetworkClient {
    val client = OkHttpClient()

    suspend fun sendChat(message: String) {
        val request = createRequest("POST", Constants.CHAT_SEND_URL,
            "message", message)
        execute(request)
    }

    suspend fun createLobby(): String? {
        val request = createRequest("GET", Constants.LOBBY_CREATE_URL)
        val response = execute(request)
        return response.body?.string()
    }

    suspend fun deleteLobby(lobbyCode: String) {
        val request = createRequest("DELETE", Constants.LOBBY_RESOURCE_URL.replace("{id}", lobbyCode))
        execute(request)
    }

    suspend fun joinLobby(lobbyCode: String, playerName: String) {
        val request = createRequest("PUT", Constants.LOBBY_PLAYER_URL.replace("{id}", lobbyCode),
            "uuid", SseClientService.uuid.toString(),
            "name", playerName)
        execute(request)
    }

    suspend fun leaveLobby(lobbyCode: String) {
        val request = createRequest("DELETE", Constants.LOBBY_PLAYER_URL.replace("{id}", lobbyCode),
            "uuid", SseClientService.uuid.toString())
        execute(request)
    }

    suspend fun startGame(lobbyCode: String) {
        val request = createRequest("GET", Constants.LOBBY_START_GAME_URL.replace("{id}", lobbyCode))
        execute(request)
    }

    private fun createRequest(method: String, path: String, vararg params: String): Request {
        val body = if (params.isNotEmpty()) {
            MultipartBody.Builder()
                .apply {
                    for (i in params.indices step 2) {
                        addFormDataPart(params[i], params[i + 1])
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun execute(request: Request): Response {
        val call = client.newCall(request)
        return withContext(Dispatchers.IO) {
            return@withContext call.execute()
        }
    }
}