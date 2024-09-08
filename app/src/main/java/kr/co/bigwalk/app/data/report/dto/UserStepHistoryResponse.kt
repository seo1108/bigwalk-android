package kr.co.bigwalk.app.data.report.dto

import java.util.*

data class UserStepHistoryResponse(
    val days: List<Date>,
    val months: List<String>
)