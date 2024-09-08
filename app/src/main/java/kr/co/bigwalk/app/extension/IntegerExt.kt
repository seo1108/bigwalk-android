package kr.co.bigwalk.app.extension

import java.text.DecimalFormat

fun Int.valueToCommaString(): String {
    return DecimalFormat("#,###").format(this).toString()
}

fun Int.valueToCommaString(reductionUnit: Int): String {
    return DecimalFormat("#,###").format(this/reductionUnit).toString()
}
