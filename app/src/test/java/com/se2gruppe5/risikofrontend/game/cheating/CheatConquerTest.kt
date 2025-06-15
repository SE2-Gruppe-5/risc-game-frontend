package com.se2gruppe5.risikofrontend.game.cheating

import okhttp3.*
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.UUID

class CheatConquerTest {
    private val client = mock<OkHttpClient>()
    private val call = mock<Call>()
    private val response = mock<Response>()

    private val networkClient = object {
        suspend fun cheatConquer(
            gameId: UUID,
            playerId: UUID,
            territoryId: Int
        ): Result<Unit> {
            return try {
                val url = "http://10.0.2.2:8080/game/$gameId/cheat/conquer" +
                        "?playerId=$playerId&territoryId=$territoryId"

                val request = Request.Builder()
                    .url(url)
                    .post(RequestBody.create(null, ByteArray(0)))
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

    @Test
    fun `cheatConquer returns success on HTTP 200`() {
        // Given
        whenever(client.newCall(org.mockito.kotlin.any())).thenReturn(call)
        whenever(call.execute()).thenReturn(response)
        whenever(response.isSuccessful).thenReturn(true)

        // When
        val result = runCatching {
            kotlinx.coroutines.runBlocking {
                networkClient.cheatConquer(UUID.randomUUID(), UUID.randomUUID(), 1)
            }
        }

        // Then
        assertTrue(result.getOrThrow().isSuccess)
    }

    @Test
    fun `cheatConquer returns failure on HTTP 500`() {
        // Given
        whenever(client.newCall(org.mockito.kotlin.any())).thenReturn(call)
        whenever(call.execute()).thenReturn(response)
        whenever(response.isSuccessful).thenReturn(false)
        whenever(response.code).thenReturn(500)

        // When
        val result = runCatching {
            kotlinx.coroutines.runBlocking {
                networkClient.cheatConquer(UUID.randomUUID(), UUID.randomUUID(), 1)
            }
        }

        // Then
        assertTrue(result.getOrThrow().isFailure)
        assertTrue(result.getOrThrow().exceptionOrNull()?.message?.contains("Server error") == true)
    }

    @Test
    fun `cheatConquer returns failure on Exception`() {
        // Given
        whenever(client.newCall(org.mockito.kotlin.any())).thenThrow(RuntimeException("Connection failed"))

        // When
        val result = runCatching {
            kotlinx.coroutines.runBlocking {
                networkClient.cheatConquer(UUID.randomUUID(), UUID.randomUUID(), 1)
            }
        }

        // Then
        assertTrue(result.getOrThrow().isFailure)
        assertTrue(result.getOrThrow().exceptionOrNull()?.message?.contains("Connection failed") == true)
    }
}
