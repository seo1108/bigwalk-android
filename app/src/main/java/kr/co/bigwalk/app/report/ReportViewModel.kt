package kr.co.bigwalk.app.report

import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.fragment.app.FragmentManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.report.dto.*
import kr.co.bigwalk.app.data.report.repository.ReportDataSource
import kr.co.bigwalk.app.data.report.repository.ReportRepository
import kr.co.bigwalk.app.dialog.ListDialog
import kr.co.bigwalk.app.dialog.MonthElement
import kr.co.bigwalk.app.dialog.WeekElement
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showDebugToast
import kr.co.bigwalk.app.util.showToast
import java.text.SimpleDateFormat
import java.util.*

class ReportViewModel(val navigator: BaseNavigator, private val fragmentManager: FragmentManager) {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    val bottomSheetTitle: ObservableField<String> = ObservableField(navigator.getContext().resources.getString(R.string.select_lookup_month))
    val selectableItems: ObservableList<String> = ObservableArrayList()
    val selectedMonth: ObservableField<MonthElement> = ObservableField(MonthElement(Date()))
    val selectedWeek: ObservableField<WeekElement> = ObservableField(WeekElement(selectedMonth.get(), 0))
    val isSelectMonth: ObservableBoolean = ObservableBoolean(true)

//
//    // 통계 데이터
//    val monthStats: ObservableField<MonthStatsResponse> = ObservableField()
//    val weeklyAnalysis: ObservableField<UserStatisticsResponse> = ObservableField()
//    val weeklyAverageSteps : ObservableField<Long> = ObservableField(0L)
//    val barData: ObservableField<BarData> = ObservableField()
//    val yxis: ObservableList<String> = ObservableArrayList()

//    init {
//        getMonthlyStatistics(SimpleDateFormat("yyyy.MM", Locale.KOREA).format(Date()))
//    }

    fun finishActivity() {
        navigator.finish()
    }

//    fun showMonthlyBottomSheet() {
//        ReportRepository.getUserStepHistory(object : ReportDataSource.GetUserStepHistoryCallback {
//            override fun onSuccess(stepHistoryResponse: UserStepHistoryResponse) {
//                selectableItems.clear()
//                selectableItems.addAll(stepHistoryResponse.months)
//                bottomSheetTitle.set(navigator.getContext().resources.getString(R.string.select_lookup_month))
//                isSelectMonth.set(true)
//
//                val listDialog = ListDialog.newInstance(this@ReportViewModel)
//                listDialog.show(fragmentManager, listDialog.tag)
//            }
//
//            override fun onFailed(reason: String) {
//                showToast("월 목록을 불러올 수 없습니다. 다시 시도해주세요.")
//                showDebugToast("월 목록을 불러올 수 없습니다. 다시 시도해주세요.")
//            }
//        })
//    }
//
//    fun showWeeklyBottomSheet() {
//        bottomSheetTitle.set(navigator.getContext().resources.getString(R.string.select_lookup_week))
//        isSelectMonth.set(false)
//        val calendar = Calendar.getInstance()
//        calendar.time = selectedMonth.get()?.date
//        val weekOfMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
//
//        selectableItems.clear()
//        for (i in 0..weekOfMonth) {
//            selectableItems.add(WeekElement(selectedMonth.get(), i).getElementTitle())
//        }
//
//        val listDialog = ListDialog.newInstance(this)
//        listDialog.show(fragmentManager, listDialog.tag)
//    }
//
//    fun finishBottomSheet() {
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//    }

//    fun getMonthlyStatistics(yearMonth: String) {
//        ReportRepository.getMonthlyStats(yearMonth, object : ReportDataSource.GetMonthlyStatsCallback {
//            override fun onSuccess(monthStatsResponse: MonthStatsResponse) {
//                DebugLog.d("월간통계: $monthStatsResponse")
//                monthStats.set(monthStatsResponse)
//                barData.set(setCaloriesBarChart(monthStatsResponse, monthStatsResponse.ordinalWeekStats))


//                weeklyAnalysis.set(userStatisticsResponse)
//                weeklyAverageSteps.set(userStatisticsResponse.average)
//                barData.set(setBarChartData(userStatisticsResponse.walkStatistics))
//                navigator.refreshWalkFragment(monthStatsResponse)
//            }
//
//            override fun onFailed(reason: String) {
//                showToast("월간 걸음 통계를 불러올 수 없습니다. 다시 시도해주세요.")
//                showDebugToast("월간 걸음 통계를 불러올 수 없습니다. 다시 시도해주세요.")
//            }
//
//        })
//    }

//    fun getWeeklyStatistics() {
//
//    }

//    private fun setCaloriesBarChart(monthStatsResponse: MonthStatsResponse, weekStatsResponse : List<OrdinalWeekStatsResponse>): BarData {
//        val entries: MutableList<BarEntry> = ArrayList()
//        val colors: MutableList<Int> = ArrayList()
//
//        for ((index, response) in weekStatsResponse.withIndex()) {
//            yxis.add(response.getWeekOrder()) // 바 라벨 값
//            entries.add(BarEntry(index.toFloat(), response.totalCalories.toFloat()))
//            when (response.totalCalories) {
//                monthStatsResponse.maxAvgCaloriesWeekOrder[0].totalCalories -> {
//                    colors.add(ContextCompat.getColor(navigator.getContext(), R.color.blue))
//                }
//                monthStatsResponse.minAvgCaloriesWeekOrder[0].totalCalories -> {
//                    colors.add(ContextCompat.getColor(navigator.getContext(), R.color.sub))
//                }
//                else -> {
//                    colors.add(ContextCompat.getColor(navigator.getContext(), R.color.gray_border))
//                }
//            }
//        }
//
//        val barDataSet = BarDataSet(entries, "")
//        barDataSet.setDrawValues(true) // 바 상단 값 제거
//        barDataSet.colors = colors // 바 컬러 변경
//        return BarData(barDataSet)
//    }

}