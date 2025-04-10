package com.se2gruppe5.risikofrontend.network

import com.se2gruppe5.risikofrontend.Constants
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
        val request = Request.Builder()
            .url(Constants.HOST + Constants.CHAT_SEND_URL)
            .post(MultipartBody.Builder()
                .addFormDataPart("message", message)
                .build())
            .build()
        execute(request)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun execute(request: Request): Response {
        val call = client.newCall(request)
        return withContext(Dispatchers.IO) {
            return@withContext call.execute()
        }
    }
}