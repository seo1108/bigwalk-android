package kr.co.bigwalk.app.report.weekly

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseFragment
import kr.co.bigwalk.app.data.report.dto.DayOfWeekReportResponse
import kr.co.bigwalk.app.databinding.FragmentWeeklyReportBinding
import kr.co.bigwalk.app.report.Report2ViewModel
import kr.co.bigwalk.app.util.gaSendLogEvent

class WeeklyReportFragment : BaseFragment<FragmentWeeklyReportBinding>(R.layout.fragment_weekly_report) {
    private val report2ViewModel by activityViewModels<Report2ViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        bindView()
    }

    private fun setView() {
        with(binding) {
            vm = report2ViewModel
            previousButton.setOnClickListener {
                report2ViewModel.getStepReportFromWeek(-1)
                gaSendLogEvent(requireContext(), "report_prev_period_btn_tap")
            }
            nextButton.setOnClickListener {
                report2ViewModel.getStepReportFromWeek(1)
                gaSendLogEvent(requireContext(), "report_next_period_btn_tap")
            }
        }
    }

    private fun setBarChart(dayOfWeekReportResponse: DayOfWeekReportResponse) {
        with(binding) {
            barChart.run {
                isDragEnabled = false
                description.isEnabled = false //차트 옆에 별도로 표기되는 description이다. false로 설정하여 안보이게 했다.
                setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 정해주었다.
                setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
                isDoubleTapToZoomEnabled = false
                setDrawBarShadow(false)//그래프의 그림자
                setDrawGridBackground(false)//격자구조 넣을건지
                axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                    spaceTop = 0f
//                    axisMaximum = dayOfWeekReportResponse.max.dailySteps.toFloat() //100 위치에 선을 그리기 위해 101f로 맥시멈을 정해주었다 // 받은 response의 최대 걸음 수를 max로 설정한다.
                    axisMinimum = 0f // 최소값 0
                    granularity = 50f // 50 단위마다 선을 그리려고 granularity 설정 해 주었다.
                    //위 설정이 20f였다면 총 5개의 선이 그려졌을 것
                    setDrawLabels(false) // 값 적는거 허용 (0, 50, 100)
                    setDrawGridLines(false) //격자 라인 활용
                    setDrawAxisLine(false) // 축 그리기 설정
                    axisLineColor = ContextCompat.getColor(context, R.color.red) // 축 색깔 설정
                    gridColor = ContextCompat.getColor(context, R.color.green) // 축 아닌 격자 색깔 설정
                    textColor = ContextCompat.getColor(context, R.color.blue) // 라벨 텍스트 컬러 설정
                    textSize = 14f //라벨 텍스트 크기
                }
                xAxis.run {
                    axisMinimum = 0f
                    axisMaximum = 7f
                    setCenterAxisLabels(true)
                    position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                    granularity = 1f // 1 단위만큼 간격 두기
                    setDrawAxisLine(false) // 축 그림
                    setDrawGridLines(false) // 격자
                    textColor = ContextCompat.getColor(context, R.color.sub) //라벨 색상
                    valueFormatter = IndexAxisValueFormatter(arrayOf("월", "화", "수", "목", "금", "토", "일"))
                    textSize = 10f // 텍스트 크기
                }
                axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//                setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
                animateY(800) // 밑에서부터 올라오는 애니매이션 적용
                legend.run {
                    isEnabled = false //차트 범례 설정
//                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
//                    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
//                    yEntrySpace = -3f
//                    orientation = Legend.LegendOrientation.VERTICAL
//                    setDrawInside(true)
//                    textColor = ContextCompat.getColor(context!!, R.color.sub)
                }

                val stepEntries = ArrayList<BarEntry>()
                val donateStepEntries = ArrayList<BarEntry>()
                dayOfWeekReportResponse.reportWalks.forEachIndexed { index, reportWalkResponse ->
                    stepEntries.add(BarEntry(index + 1f, reportWalkResponse.dailySteps.toFloat()))
                    donateStepEntries.add(BarEntry(index + 1f, reportWalkResponse.dailyDonationSteps.toFloat()))
                }

                val stepDataSet = BarDataSet(stepEntries, TAG_STEP).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.gray_border)
                    highLightColor = ContextCompat.getColor(requireContext(), R.color.sub2)
                    highLightAlpha = 255
                }

                val donateStepDataSet = BarDataSet(donateStepEntries, TAG_DONATE_STEP).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.main_point_disable)
                    highLightColor = ContextCompat.getColor(requireContext(), R.color.theme_174dfe)
                    highLightAlpha = 255
                }

                val data = BarData(stepDataSet, donateStepDataSet)
                data.barWidth = 0.215f//막대 너비 설정하기
                this.data = data //차트의 데이터를 data로 설정해줌.
                setFitBars(true)
                groupBars(0.01f, 0.50f, 0.035f)
                invalidate()
            }
            barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if ((h?.y ?: 0f) < 1f) {
                        report2ViewModel.unSelectStepReport()
                        return
                    }
                    val stepHighLight = Highlight(h?.x ?: 0f, 0, -1)
                    val donateStepHighLight = Highlight(h?.x ?: 0f, 1, -1)
                    barChart.highlightValues(arrayOf(stepHighLight, donateStepHighLight))
                    report2ViewModel.selectStepReport()
                    gaSendLogEvent(requireContext(), "report_selected_date_databox_view")
                    report2ViewModel.replaceSelectStepReport(h?.x?.toInt() ?: 0)
                }

                override fun onNothingSelected() {
                    report2ViewModel.unSelectStepReport()
                }

            })
        }
    }

    private fun bindView() {
        with(report2ViewModel) {
            dayOfWeekReportResponse.observe(viewLifecycleOwner, Observer {
                setBarChart(it)
            })
            isShowingSelectData.observe(viewLifecycleOwner, Observer {
                if (!it) binding.barChart.highlightValues(null)
            })
        }
    }

    companion object {
        private const val TAG_STEP = "걸음 수"
        private const val TAG_DONATE_STEP = "기부 걸음 수"
        fun newInstance() =
            WeeklyReportFragment()
    }
}