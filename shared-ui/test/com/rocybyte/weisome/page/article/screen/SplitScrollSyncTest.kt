package com.rocybyte.weisome.page.article.screen

import kotlin.test.Test
import kotlin.test.assertEquals

class SplitScrollSyncTest {
    @Test
    /** Verifies the offset lookup hits the covering range, including both endpoints. */
    fun `finds block containing offset`() {
        val ranges = listOf(0..5, 10..20, 21..21)

        assertEquals(0, blockIndexForOffset(ranges, 0))
        assertEquals(0, blockIndexForOffset(ranges, 5))
        assertEquals(1, blockIndexForOffset(ranges, 10))
        assertEquals(1, blockIndexForOffset(ranges, 20))
        assertEquals(2, blockIndexForOffset(ranges, 21))
    }

    @Test
    /** Verifies offsets inside gaps between blocks and empty inputs report no block. */
    fun `returns minus one when no block contains offset`() {
        val ranges = listOf(0..5, 10..20)

        assertEquals(-1, blockIndexForOffset(ranges, 6))
        assertEquals(-1, blockIndexForOffset(ranges, 25))
        assertEquals(-1, blockIndexForOffset(emptyList(), 0))
    }

    @Test
    /** Verifies the topmost block at a scroll position is the closest one at or above it. */
    fun `finds topmost block at scroll position`() {
        val tops = mapOf(0 to 0f, 1 to 120f, 2 to 500f)

        assertEquals(0, topBlockIndexForScroll(tops, 0f))
        assertEquals(1, topBlockIndexForScroll(tops, 150f))
        assertEquals(2, topBlockIndexForScroll(tops, 600f))
    }

    @Test
    /** Verifies scrolling between two blocks still anchors to the earlier block. */
    fun `falls back to earlier block between blocks`() {
        val tops = mapOf(0 to 0f, 1 to 120f, 2 to 500f)

        assertEquals(0, topBlockIndexForScroll(tops, 100f))
        assertEquals(1, topBlockIndexForScroll(tops, 480f))
    }

    @Test
    /** Verifies the 1px tolerance and that positions above every block report none. */
    fun `tolerates one pixel and rejects scroll above first block`() {
        assertEquals(0, topBlockIndexForScroll(mapOf(0 to 5f), 4.2f))
        assertEquals(-1, topBlockIndexForScroll(mapOf(0 to 5f), 3f))
        assertEquals(-1, topBlockIndexForScroll(emptyMap(), 0f))
    }
}
