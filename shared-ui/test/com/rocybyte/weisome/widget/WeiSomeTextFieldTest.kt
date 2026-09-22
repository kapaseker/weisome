package com.rocybyte.weisome.widget

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WeiSomeTextFieldTest {
    @Test
    /** Verifies scrolling up when the span starts above the visible window. */
    fun `scrolls up to reveal span above viewport`() {
        assertEquals(120, bringIntoViewScroll(top = 120f, bottom = 150f, scrollValue = 300, viewport = 500))
    }

    @Test
    /** Verifies scrolling down when the span ends below the visible window. */
    fun `scrolls down to reveal span below viewport`() {
        assertEquals(440, bringIntoViewScroll(top = 900f, bottom = 940f, scrollValue = 300, viewport = 500))
    }

    @Test
    /** Verifies no scroll when the span is already fully visible. */
    fun `keeps scroll when span is visible`() {
        assertNull(bringIntoViewScroll(top = 350f, bottom = 370f, scrollValue = 300, viewport = 500))
        assertNull(bringIntoViewScroll(top = 300f, bottom = 800f, scrollValue = 300, viewport = 500))
    }

    @Test
    /** Verifies layout is skipped before the viewport has been measured. */
    fun `ignores unmeasured viewport`() {
        assertNull(bringIntoViewScroll(top = 700f, bottom = 740f, scrollValue = 0, viewport = 0))
    }
}
