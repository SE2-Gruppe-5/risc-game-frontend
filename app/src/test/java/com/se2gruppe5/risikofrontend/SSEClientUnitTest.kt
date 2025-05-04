package com.se2gruppe5.risikofrontend

import org.junit.Test

import org.junit.Assert.*

/**
 * Unit Tests for SSEClient.kt
 */
class SSEClientUnitTest {
    @Test
    fun testInitialization() {
        val sseClient = SSEClient();
        assertNotNull(sseClient);
    }
}