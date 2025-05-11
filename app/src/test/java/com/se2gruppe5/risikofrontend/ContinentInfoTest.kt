package com.se2gruppe5.risikofrontend


import com.se2gruppe5.risikofrontend.game.popup.ContinentInfo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ContinentInfoTest {

    private lateinit var testData: List<ContinentInfo>

    @Before
    fun setUp() {
        testData = listOf(
            ContinentInfo("Power Supply", "#b5edf9", 6, 2),
            ContinentInfo("MMC", "#e8f3a8", 2, 2),
            ContinentInfo("RAM", "#87deb3", 10, 2),
            ContinentInfo("DCON", "#f0a8e1", 5, 2),
            ContinentInfo("CPU", "#e6afaf", 6, 2),
            ContinentInfo("Essentials", "#fec466", 7, 2),
            ContinentInfo("Southbridge", "#f5d557", 4, 2),
            ContinentInfo("Wireless Mesh", "#9effad", 8, 2),
            ContinentInfo("Embedded Controller", "#9b3e3e", 7, 2),
            ContinentInfo("CMOS", "#a4e5e2", 5, 2)
        )
    }

    @Test
    fun `ContinentInfo should store correct values`() {
        val component = ContinentInfo("CPU", "#e6afaf", 6, 2)

        assertEquals("CPU", component.name)
        assertEquals("#e6afaf", component.colorHex)
        assertEquals(6, component.regions)
        assertEquals(2, component.bonus)
    }

    @Test
    fun `ContinentInfo instances with same data should be equal`() {
        val a = ContinentInfo("RAM", "#87deb3", 10, 2)
        val b = ContinentInfo("RAM", "#87deb3", 10, 2)

        assertEquals(a, b)
    }

    @Test
    fun `ContinentInfo hashCode should be consistent`() {
        val component = ContinentInfo("Wireless Mesh", "#9effad", 8, 2)
        val initialHash = component.hashCode()
        val secondHash = component.hashCode()

        assertEquals(initialHash, secondHash)
    }

    @Test
    fun `Total number of fields should be 60`() {
        val totalFields = testData.sumOf { it.regions }
        assertEquals(60, totalFields)
    }

    @Test
    fun `All components should give 2 bonus troops`() {
        testData.forEach {
            assertEquals(2, it.bonus)
        }
    }
}