package com.rocybyte.weisome.ui

import com.rocybyte.weisome.generated.resources.Res
import com.rocybyte.weisome.generated.resources.ic_welcome
import kotlin.test.Test
import kotlin.test.assertNotNull

class ResourceSmokeTest {

    @Test
    fun `welcome drawable accessor is generated`() {
        assertNotNull(Res.drawable.ic_welcome)
    }
}

