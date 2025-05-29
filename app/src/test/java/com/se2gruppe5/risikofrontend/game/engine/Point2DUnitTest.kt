package com.se2gruppe5.risikofrontend.game.engine

import org.junit.Test

import org.junit.Assert.*

class Point2DUnitTest {
    @Test
    fun inst() {
        val p = Point2D(1f, 2f)
        assertNotNull(p)
    }
}
