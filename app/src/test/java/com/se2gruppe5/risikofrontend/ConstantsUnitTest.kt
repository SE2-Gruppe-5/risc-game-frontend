package com.se2gruppe5.risikofrontend

import org.junit.Test
import org.junit.Assert.*
import java.net.MalformedURLException
import java.net.URL

/**
 * Making sure Constants.kt has a valid assignment of all constants
 */
class ConstantsUnitTest {

    fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (_: MalformedURLException) {
            false
        }
    }
    @Test
    fun instantiateTest(){
        Constants()
    }
    @Test
    fun checkConstantsExistAndValid(){
        // Not Null ?
        assertNotNull(Constants.SSE_URL)
        assertNotNull(Constants.CHAT_URL)
        // Valid URL ?
        assertTrue(isValidUrl(Constants.SSE_URL))
        assertTrue(isValidUrl(Constants.CHAT_URL))
    }
}