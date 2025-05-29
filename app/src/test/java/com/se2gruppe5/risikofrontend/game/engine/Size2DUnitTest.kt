package com.se2gruppe5.risikofrontend.game.engine

import org.junit.Test

import org.junit.Assert.*

class Size2DUnitTest {
    @Test
    fun inst() {
        val p = Size2D(1f, 2f)
        assertNotNull(p)
    }
}
