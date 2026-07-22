package com.rocybyte.weisome

import com.rocybyte.weisome.window.SavedWindowState
import java.awt.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WindowBoundsTest {
    private val primary = Rectangle(0, 0, 1920, 1040)
    private val secondary = Rectangle(-1280, 0, 1280, 1024)

    @Test
    /** Verifies visible bounds remain unchanged on their existing display. */
    fun `valid bounds remain on their current display`() {
        val saved = savedWindow(x = -1200, y = 100, width = 900, height = 700)

        assertEquals(saved, saved.clampToVisibleScreen(listOf(primary, secondary), primary))
    }

    @Test
    /** Verifies partially hidden bounds are moved fully into the best matching display. */
    fun `partially off-screen bounds are moved into the overlapping display`() {
        val saved = savedWindow(x = 1700, y = 900, width = 800, height = 600)

        assertEquals(
            saved.copy(x = 1120, y = 440),
            saved.clampToVisibleScreen(listOf(primary, secondary), primary),
        )
    }

    @Test
    /** Verifies bounds from a disconnected display fall back to the default display. */
    fun `disconnected display falls back to the default display`() {
        val saved = savedWindow(x = 3000, y = 200, width = 1000, height = 700)

        assertEquals(
            saved.copy(x = 920, y = 200),
            saved.clampToVisibleScreen(listOf(primary, secondary), primary),
        )
    }

    @Test
    /** Verifies a window larger than its display is reduced to the available work area. */
    fun `oversized bounds shrink to the target work area`() {
        val saved = savedWindow(x = -200, y = -100, width = 2400, height = 1400)

        assertEquals(
            saved.copy(x = 0, y = 0, width = 1920, height = 1040),
            saved.clampToVisibleScreen(listOf(primary), primary),
        )
    }

    @Test
    /** Verifies restoration is abandoned when no usable display information exists. */
    fun `missing displays produce no restorable bounds`() {
        assertNull(savedWindow().clampToVisibleScreen(emptyList(), null))
    }

    /** Creates a saved-window-state fixture for display-boundary assertions. */
    private fun savedWindow(
        x: Int = 0,
        y: Int = 0,
        width: Int = 1280,
        height: Int = 720,
    ) = SavedWindowState(
        x = x,
        y = y,
        width = width,
        height = height,
        isMaximized = false,
    )
}
