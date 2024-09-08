package kr.co.bigwalk.app.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun longValueToCommaString(value: Long): String {
    return DecimalFormat("#,###").format(value).toString()
}

fun intValueToCommaString(value: Int): String {
    return DecimalFormat("#,###").format(value).toString()
}


// https://codechacha.com/ko/kotlin-convert-string-to-long/
fun getCompleteWord(name: String, firstValue: String, secondValue: String?): String {
    val lastName = name[name.length - 1]

    if (lastName.code < 0xAC00 || lastName.code > 0xD7A3) {
        return name
    }
    val seletedValue = if ((lastName.code - 0xAC00) % 28 > 0) firstValue else secondValue!!
    return name + seletedValue
}

fun getTodayDate(): String {
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    var strMon = ""
    var strDay = ""

    if (month >= 10) {
        strMon = month.toString()
    } else {
        strMon = "0$month"
    }

    if (dayOfMonth >= 10) {
        strDay = dayOfMonth.toString()
    } else {
        strDay = "0$dayOfMonth"
    }

    return "$year$strMon$strDay"

}
fun dayDiff(beforeDate: String, currentDate: String) : Long {
    val dateFormat = SimpleDateFormat("yyyyMMdd")

    val startDate = dateFormat.parse(beforeDate).time
    val endDate = dateFormat.parse(currentDate).time
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.time

    return (endDate - startDate) / (24 * 60 * 60 * 1000)
}
