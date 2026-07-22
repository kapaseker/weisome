package com.rocybyte.weisome.navigation

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class WeiSomeRouteTest {
    @Test
    fun `article route survives serialization round trip`() {
        val encoded = Json.encodeToString<WeiSomeRoute>(WechatArticleRoute)

        assertEquals(WechatArticleRoute, Json.decodeFromString<WeiSomeRoute>(encoded))
    }
}
