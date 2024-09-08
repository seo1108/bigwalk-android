package kr.co.bigwalk.app.data.report.repository

import kr.co.bigwalk.app.data.report.dto.MonthStatsResponse
import kr.co.bigwalk.app.data.report.dto.UserStatisticsResponse
import kr.co.bigwalk.app.data.report.dto.UserStepHistoryResponse

interface ReportDataSource {

    interface GetUserStatisticsCallback {
        fun onSuccess(userStatisticsResponse: UserStatisticsResponse)
        fun onFailed(reason: String)
    }

    fun getWalkStatistics(startDate: String, endDate: String, getUserStatisticsCallback: GetUserStatisticsCallback)

    fun getDonationStatistics(startDate: String, endDate: String, getUserStatisticsCallback: GetUserStatisticsCallback)

    interface GetUserStepHistoryCallback {
        fun onSuccess(stepHistoryResponse: UserStepHistoryResponse)
        fun onFailed(reason: String)
    }

    fun getUserStepHistory(getUserStepHistoryCallback: GetUserStepHistoryCallback)

    interface GetMonthlyStatsCallback {
        fun onSuccess(monthStatsResponse: MonthStatsResponse)
        fun onFailed(reason: String)
    }

    fun getMonthlyStats(date: String, callback: GetMonthlyStatsCallback)
}