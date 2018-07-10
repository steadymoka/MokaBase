package io.moka.lib.adhelper

import io.moka.lib.MokaBase

object Contract {

    var debuggable: Boolean
        get() = MokaBase.debuggable
        set(value) {
            MokaBase.debuggable = value
        }

}