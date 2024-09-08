package kr.co.bigwalk.app.data.report.repository

object ReportRepository : ReportDataSource {

    override fun getWalkStatistics(startDate: String, endDate: String, getUserStatisticsCallback: ReportDataSource.GetUserStatisticsCallback) {
        ReportRemoteDataSource.getWalkStatistics(startDate, endDate, getUserStatisticsCallback)
    }

    override fun getDonationStatistics(startDate: String, endDate: String, getUserStatisticsCallback: ReportDataSource.GetUserStatisticsCallback) {
        ReportRemoteDataSource.getDonationStatistics(startDate, endDate, getUserStatisticsCallback)
    }

    override fun getUserStepHistory(getUserStepHistoryCallback: ReportDataSource.GetUserStepHistoryCallback) {
        ReportRemoteDataSource.getUserStepHistory(getUserStepHistoryCallback)
    }

    override fun getMonthlyStats(date: String, callback: ReportDataSource.GetMonthlyStatsCallback) {
        ReportRemoteDataSource.getMonthlyStats(date, callback)
    }

}