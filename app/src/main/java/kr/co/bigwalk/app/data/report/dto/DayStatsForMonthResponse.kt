package kr.co.bigwalk.app.data.report.dto

import java.util.*

data class DayStatsForMonthResponse(
    val localDate: Date,
    val day: Int,
    val overTenThousandSteps: Boolean,
    val overSevenThousandSteps: Boolean,
    val overFiveThousandSteps: Boolean,
    val dayOfWeek: String,
    val month: Int,
    val korDay: String
)