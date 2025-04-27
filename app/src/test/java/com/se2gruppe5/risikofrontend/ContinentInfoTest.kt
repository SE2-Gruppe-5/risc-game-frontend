package com.se2gruppe5.risikofrontend


import com.se2gruppe5.risikofrontend.Popup.ContinentInfo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ContinentInfoTest {

    private lateinit var testData: List<ContinentInfo>

    @Before
    fun setUp() {
        testData = listOf(
            ContinentInfo("Europa", "#4CAF50", 6, 5),
            ContinentInfo("Asien", "#E91E63", 7, 7),
            ContinentInfo("Afrika", "#F44336", 6, 3)
        )
    }

    @Test
    fun `ContinentInfo should store correct values`() {
        val continent = ContinentInfo("Europa", "#4CAF50", 6, 5)

        assertEquals("Europa", continent.name)
        assertEquals("#4CAF50", continent.colorHex)
        assertEquals(6, continent.regions)
        assertEquals(5, continent.bonus)
    }
    @Test
    fun `ContinentInfo instances with same data should be equal`() {
        val a = ContinentInfo("Afrika", "#F44336", 6, 3)
        val b = ContinentInfo("Afrika", "#F44336", 6, 3)

        assertEquals(a, b)
    }
    @Test
    fun `ContinentInfo hashCode should be consistent`() {
        val continent = ContinentInfo("Asien", "#E91E63", 7, 7)
        val initialHash = continent.hashCode()
        val secondHash = continent.hashCode()

        assertEquals(initialHash, secondHash)
    }
}