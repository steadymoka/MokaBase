package io.moka.lib.authentication.server

import io.moka.lib.base.MokaBase


internal object ServerInfo {

    private const val END_POINT_API = "https://api.haruharu.io/"

    val endPoint: String
        get() = END_POINT_API

    val prefix: String
        get() = if (MokaBase.DEBUG) "dev" else "v1"
}
