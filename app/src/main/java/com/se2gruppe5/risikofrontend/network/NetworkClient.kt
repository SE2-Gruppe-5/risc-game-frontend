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
        val request = createRequest("POST", Constants.CHAT_SEND_URL,
            "message", message)
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