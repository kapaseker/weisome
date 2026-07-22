package com.rocybyte.weisome.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface WeiSomeRoute : NavKey

@Serializable
internal data object WechatArticleRoute : WeiSomeRoute
