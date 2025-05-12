package com.se2gruppe5.risikofrontend


import com.se2gruppe5.risikofrontend.game.dataclasses.ContinentInfoRecord
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ContinentInfoTest {

    private lateinit var testData: List<ContinentInfoRecord>

    @Before
    fun setUp() {
        testData = listOf(
            ContinentInfoRecord("Power Supply", "#b5edf9", 6, 2),
            ContinentInfoRecord("MMC", "#e8f3a8", 2, 2),
            ContinentInfoRecord("RAM", "#87deb3", 10, 2),
            ContinentInfoRecord("DCON", "#f0a8e1", 5, 2),
            ContinentInfoRecord("CPU", "#e6afaf", 6, 2),
            ContinentInfoRecord("Essentials", "#fec466", 7, 2),
            ContinentInfoRecord("Southbridge", "#f5d557", 4, 2),
            ContinentInfoRecord("Wireless Mesh", "#9effad", 8, 2),
            ContinentInfoRecord("Embedded Controller", "#9b3e3e", 7, 2),
            ContinentInfoRecord("CMOS", "#a4e5e2", 5, 2)
        )
    }

    @Test
    fun `ContinentInfo should store correct values`() {
        val component = ContinentInfoRecord("CPU", "#e6afaf", 6, 2)

        assertEquals("CPU", component.name)
        assertEquals("#e6afaf", component.colorHex)
        assertEquals(6, component.regions)
        assertEquals(2, component.bonus)
    }

    @Test
    fun `ContinentInfo instances with same data should be equal`() {
        val a = ContinentInfoRecord("RAM", "#87deb3", 10, 2)
        val b = ContinentInfoRecord("RAM", "#87deb3", 10, 2)

        assertEquals(a, b)
    }

    @Test
    fun `ContinentInfo hashCode should be consistent`() {
        val component = ContinentInfoRecord("Wireless Mesh", "#9effad", 8, 2)
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