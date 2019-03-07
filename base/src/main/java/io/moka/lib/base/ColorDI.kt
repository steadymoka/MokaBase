package io.moka.lib.base

import io.moka.lib.base.util.color

object ColorDI {

    var primaryColor: Int = 0
        get() {
            if (field == 0) {
                return color(R.color.black_06_sub_text)
            }

            return field
        }

    var primaryDarkColor: Int = 0
        get() {
            if (field == 0) {
                return color(R.color.black_03_text)
            }

            return field
        }

}