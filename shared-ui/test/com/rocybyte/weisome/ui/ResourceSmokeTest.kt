package com.rocybyte.weisome.ui

import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.app_name
import com.rocybyte.weisome.generated.resources.ic_welcome
import kotlin.test.Test
import kotlin.test.assertNotNull

class ResourceSmokeTest {

    @Test
    fun `welcome drawable accessor is generated`() {
        assertNotNull(Res.drawable.ic_welcome)
    }

    @Test
    fun `app name string accessor is generated`() {
        assertNotNull(Res.string.app_name)
    }
}
