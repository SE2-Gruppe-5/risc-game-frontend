package com.se2gruppe5.risikofrontend

import org.junit.Test
import org.junit.Assert.*
import java.net.MalformedURLException
import java.net.URL

/**
 * Making sure Constants.kt has a valid assignment of all constants
 */
class ConstantsUnitTest {

    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (_: MalformedURLException) {
            false
        }
    }

    private fun isValidEndpoint(endpoint: String): Boolean {
        return endpoint.startsWith("/")
    }

    @Test
    fun instantiateTest() {
        Constants()
    }

    @Test
    fun checkConstantsExist() {
        assertNotNull(Constants.HOST)
        assertNotNull(Constants.SSE_URL)
        assertNotNull(Constants.CHAT_SEND_URL)
        assertNotNull(Constants.ASSIGN_TERRITORIES_URL)
        assertNotNull(Constants.DISTRIBUTE_TROOPS_URL)
    }

    @Test
    fun checkHostValid() {
        // Valid URL ?
        assertTrue(isValidUrl(Constants.HOST))
    }

    @Test
    fun checkEndpointsValid() {
        // Starts with backslash ?
        assertTrue(isValidEndpoint(Constants.SSE_URL))
        assertTrue(isValidEndpoint(Constants.CHAT_SEND_URL))
    }

    @Test
    fun checkAssignTerritoriesUrlValid() {
        // Überprüfen, ob die URL für die Zuweisung von Territorien eine gültige URL ist
        assertTrue(isValidUrl(Constants.ASSIGN_TERRITORIES_URL))
    }

    @Test
    fun checkDistributeTroopsUrlValid() {
        // Überprüfen, ob die URL für das Verteilen von Truppen eine gültige URL ist
        assertTrue(isValidUrl(Constants.DISTRIBUTE_TROOPS_URL))
    }

    @Test
    fun checkAssignTerritoriesUrlEndpoint() {
        // Überprüfen, ob der Endpunkt mit einem "/" beginnt
        assertTrue(isValidEndpoint(Constants.ASSIGN_TERRITORIES_URL))
    }

    @Test
    fun checkDistributeTroopsUrlEndpoint() {
        // Überprüfen, ob der Endpunkt mit einem "/" beginnt
        assertTrue(isValidEndpoint(Constants.DISTRIBUTE_TROOPS_URL))
    }
}