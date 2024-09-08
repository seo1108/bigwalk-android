package kr.co.bigwalk.app.report

import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.report.dto.MonthStatsResponse

interface ReportNavigator: BaseNavigator {
    fun refreshWalkFragment(monthStatsResponse: MonthStatsResponse)
    fun refreshDonationFragment()
}