package kr.co.bigwalk.app.data.report.repository

object ReportLocalDataSource: ReportDataSource {
    override fun getWalkStatistics(
        startDate: String,
        endDate: String,
        getUserStatisticsCallback: ReportDataSource.GetUserStatisticsCallback
    ) {
        //nothing to do
    }

    override fun getDonationStatistics(
        startDate: String,
        endDate: String,
        getUserStatisticsCallback: ReportDataSource.GetUserStatisticsCallback
    ) {
        //nothing to do
    }

    override fun getUserStepHistory(getUserStepHistoryCallback: ReportDataSource.GetUserStepHistoryCallback) {

    }

    override fun getMonthlyStats(date: String, callback: ReportDataSource.GetMonthlyStatsCallback) {

    }
}