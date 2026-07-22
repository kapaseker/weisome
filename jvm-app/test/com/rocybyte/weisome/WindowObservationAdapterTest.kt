package com.rocybyte.weisome

import androidx.compose.ui.window.WindowPlacement
import com.rocybyte.weisome.window.biz.WindowMode
import kotlin.test.Test
import kotlin.test.assertEquals

class WindowObservationAdapterTest {
    @Test
    /** Verifies every Compose placement maps to its platform-independent counterpart. */
    fun `compose placements map to platform-independent window modes`() {
        assertEquals(WindowMode.Floating, WindowPlacement.Floating.toWindowMode())
        assertEquals(WindowMode.Maximized, WindowPlacement.Maximized.toWindowMode())
        assertEquals(WindowMode.Fullscreen, WindowPlacement.Fullscreen.toWindowMode())
    }
}
