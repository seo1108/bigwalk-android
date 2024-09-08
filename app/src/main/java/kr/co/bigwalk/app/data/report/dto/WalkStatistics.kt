package kr.co.bigwalk.app.data.report.dto

import java.util.*

data class WalkStatistics(
    val dailyDate: Date,
    val dayOfWeek: DayOfWeek,
    val dailySteps: Long
) {
    fun getDayOfWeekString(): String {
        if (dayOfWeek.name == "MONDAY") {
            return "월"
        } else if (dayOfWeek.name == "TUESDAY") {
            return "화"
        } else if (dayOfWeek.name == "WEDNESDAY") {
            return "수"
        } else if (dayOfWeek.name == "THURSDAY") {
            return "목"
        } else if (dayOfWeek.name == "FRIDAY") {
            return "금"
        } else if (dayOfWeek.name == "SATURDAY") {
            return "토"
        } else if (dayOfWeek.name == "SUNDAY") {
            return "일"
        } else {
            return "-"
        }
    }
}