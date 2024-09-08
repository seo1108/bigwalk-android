package kr.co.bigwalk.app.data.report.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.report.dto.MonthStatsRequest
import kr.co.bigwalk.app.data.report.dto.MonthStatsResponse
import kr.co.bigwalk.app.data.report.dto.UserStatisticsResponse
import kr.co.bigwalk.app.data.report.dto.UserStepHistoryResponse
import kr.co.bigwalk.app.exception.ReportException
import kr.co.bigwalk.app.util.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ReportRemoteDataSource : ReportDataSource {

    override fun getWalkStatistics(
        startDate: String,
        endDate: String,
        getUserStatisticsCallback: ReportDataSource.GetUserStatisticsCallback
    ) {
        RemoteApiManager.getService().getWalkStatistics(startDate, endDate)
            .enqueue(object : Callback<UserStatisticsResponse> {
                override fun onFailure(call: Call<UserStatisticsResponse>, t: Throwable) {
                    getUserStatisticsCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<UserStatisticsResponse>, response: Response<UserStatisticsResponse>) {
                    when (response.code()) {
                        200 -> response.body()?.let { getUserStatisticsCallback.onSuccess(it) }
                        else -> getUserStatisticsCallback.onFailed(response.errorBody()?.string()!!)
                    }
                }

            })
    }

    override fun getDonationStatistics(
        startDate: String,
        endDate: String,
        getUserStatisticsCallback: ReportDataSource.GetUserStatisticsCallback
    ) {
        RemoteApiManager.getService().getDonationStatistics(startDate, endDate)
            .enqueue(object : Callback<UserStatisticsResponse> {
                override fun onFailure(call: Call<UserStatisticsResponse>, t: Throwable) {
                    getUserStatisticsCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<UserStatisticsResponse>, response: Response<UserStatisticsResponse>) {
                    when (response.code()) {
                        200 -> response.body()?.let { getUserStatisticsCallback.onSuccess(it) }
                        else -> getUserStatisticsCallback.onFailed(response.errorBody()?.string()!!)
                    }
                }

            })
    }

    override fun getUserStepHistory(getUserStepHistoryCallback: ReportDataSource.GetUserStepHistoryCallback) {
        RemoteApiManager.getService().getUserStepHistory()
            .enqueue(object : Callback<UserStepHistoryResponse> {
                override fun onFailure(call: Call<UserStepHistoryResponse>, t: Throwable) {
                    getUserStepHistoryCallback.onFailed(t.localizedMessage)
                    DebugLog.e(ReportException(t.localizedMessage))
                }

                override fun onResponse(
                    call: Call<UserStepHistoryResponse>,
                    response: Response<UserStepHistoryResponse>
                ) {
                    when (response.code()) {
                        200 -> response.body()?.let { getUserStepHistoryCallback.onSuccess(it) }
                        else -> {
                            getUserStepHistoryCallback.onFailed(response.errorBody()?.string()!!)
                            DebugLog.e(ReportException(response.code().toString()))
                        }
                    }
                }

            })
    }

    override fun getMonthlyStats(date: String, callback: ReportDataSource.GetMonthlyStatsCallback) {
        RemoteApiManager.getService().getMonthlyReport(MonthStatsRequest(date))
            .enqueue(object : Callback<MonthStatsResponse> {
                override fun onFailure(call: Call<MonthStatsResponse>, t: Throwable) {
                    callback.onFailed(t.localizedMessage)
                    DebugLog.e(ReportException(t.localizedMessage))
                }

                override fun onResponse(
                    call: Call<MonthStatsResponse>,
                    response: Response<MonthStatsResponse>
                ) {
                    when (response.code()) {
                        200 -> response.body()?.let { callback.onSuccess(it) }
                        else -> {
                            callback.onFailed(response.errorBody()?.string()!!)
                            DebugLog.e(ReportException(response.code().toString()))
                        }
                    }
                }

            })
    }

}