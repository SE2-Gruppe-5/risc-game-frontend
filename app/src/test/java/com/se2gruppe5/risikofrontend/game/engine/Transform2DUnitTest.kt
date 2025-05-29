package com.se2gruppe5.risikofrontend.game.engine

import org.junit.Test

import org.junit.Assert.*

class Transform2DUnitTest {
    @Test
    fun inst() {
        val p = Transform2D(Point2D(1f,2f), Size2D(3f,4f))
        assertNotNull(p)
    }
}
