package kr.co.bigwalk.app.report.walk

import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.report.dto.UserStatisticsResponse
import kr.co.bigwalk.app.data.report.dto.WalkStatistics
import kr.co.bigwalk.app.data.report.repository.ReportDataSource
import kr.co.bigwalk.app.data.report.repository.ReportRepository
import kr.co.bigwalk.app.exception.WalkException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import java.text.SimpleDateFormat
import java.util.*

class WalkReportViewModel(val navigator: BaseNavigator) {

    val weeklyAnalysis: ObservableField<UserStatisticsResponse> = ObservableField()
    val weeklyAverageSteps : ObservableField<Long> = ObservableField(0L)
    val barData: ObservableField<BarData> = ObservableField()
    val yxis: ObservableList<String> = ObservableArrayList()

    init {
        getWeeklyReport()
    }

    private fun getWeeklyReport() {
        val beforeAWeek = Calendar.getInstance()
        beforeAWeek.add(Calendar.DAY_OF_YEAR, -6)
        ReportRepository.getWalkStatistics(parseDateFormat(beforeAWeek.time), parseDateFormat(Date()), object : ReportDataSource.GetUserStatisticsCallback {
            override fun onSuccess(userStatisticsResponse: UserStatisticsResponse) {
                DebugLog.d(userStatisticsResponse.toString())
                weeklyAnalysis.set(userStatisticsResponse)
                weeklyAverageSteps.set(userStatisticsResponse.average)
                barData.set(setBarChartData(userStatisticsResponse.walkStatistics))
            }

            override fun onFailed(reason: String) {
                showToast("걸음 리포트를 확인할 수 없습니다. 다시 시도해주세요!")
                DebugLog.e(WalkException(reason))
                navigator.finish()
            }

        })
    }

    private fun setBarChartData(dailyStatisticsResponse : List<WalkStatistics>): BarData {
        val entries: MutableList<BarEntry> = ArrayList()
        val entries2: MutableList<BarEntry> = ArrayList()
        val colors: MutableList<Int> = ArrayList()

        for ((index, response) in dailyStatisticsResponse.withIndex()) {
            yxis.add(response.getDayOfWeekString()) // 바 라벨 값
            entries.add(BarEntry(index.toFloat(), response.dailySteps.toFloat()))
            entries2.add(BarEntry(index.toFloat(), 10000*index.toFloat()))
            when (response.dailySteps) {
                weeklyAnalysis.get()?.max?.dailySteps -> {
                    colors.add(ContextCompat.getColor(navigator.getContext(), R.color.blue))
                }
                weeklyAnalysis.get()?.min?.dailySteps -> {
                    colors.add(ContextCompat.getColor(navigator.getContext(), R.color.sub))
                }
                else -> {
                    colors.add(ContextCompat.getColor(navigator.getContext(), R.color.gray_border))
                }
            }
        }

        val barDataSet = BarDataSet(entries, "GROUP 1")
        val barDataSet2 = BarDataSet(entries2, "GROUP 2")
        barDataSet.setDrawValues(true) // 바 상단 값 제거
        barDataSet.colors = colors // 바 컬러 변경
        barDataSet2.setDrawValues(true) // 바 상단 값 제거
        barDataSet2.colors = colors // 바 컬러 변경
        return BarData(barDataSet, barDataSet2)
    }

    private fun parseDateFormat(date: Date): String {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        return dateFormatter.format(date)
    }
}