package com.rocybyte.weisome

import androidx.compose.ui.window.WindowPlacement
import com.rocybyte.weisome.window.biz.WindowMode
import kotlin.test.Test
import kotlin.test.assertEquals

class WindowObservationAdapterTest {
    @Test
    fun `compose placements map to platform-independent window modes`() {
        assertEquals(WindowMode.Floating, WindowPlacement.Floating.toWindowMode())
        assertEquals(WindowMode.Maximized, WindowPlacement.Maximized.toWindowMode())
        assertEquals(WindowMode.Fullscreen, WindowPlacement.Fullscreen.toWindowMode())
    }
}
