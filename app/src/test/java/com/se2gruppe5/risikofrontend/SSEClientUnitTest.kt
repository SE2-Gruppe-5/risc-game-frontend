package com.se2gruppe5.risikofrontend

import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import org.junit.Test

import org.junit.Assert.*

/**
 * Unit Tests for SSEClient.kt
 */
class SSEClientUnitTest {
    @Test
    fun testInitialization() {
        val sseClient = SseClientService();
        assertNotNull(sseClient);
    }
}