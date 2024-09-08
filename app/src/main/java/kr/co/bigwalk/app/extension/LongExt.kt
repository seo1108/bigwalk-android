package kr.co.bigwalk.app.extension

import java.text.DecimalFormat

fun Long.valueToCommaString(): String {
    return DecimalFormat("#,###").format(this).toString()
}

fun Long.valueToCommaString(reductionUnit: Int): String {
    return DecimalFormat("#,###").format(this / reductionUnit).toString()
}

fun Long.numberToUnit(): String {
    return if (this >= 1000L) {
        (this / 1000).toString() + "k"
    } else {
        this.toString()
    }
}
