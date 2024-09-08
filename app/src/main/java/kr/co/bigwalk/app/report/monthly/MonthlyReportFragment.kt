package kr.co.bigwalk.app.report.monthly

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseFragment
import kr.co.bigwalk.app.data.report.dto.DayOfMonthReportResponse
import kr.co.bigwalk.app.data.report.dto.ReportWalkResponse
import kr.co.bigwalk.app.databinding.FragmentMonthlyReportBinding
import kr.co.bigwalk.app.report.Report2ViewModel
import kr.co.bigwalk.app.util.gaSendLogEvent
import kr.co.bigwalk.app.util.showToast
import java.text.SimpleDateFormat
import java.util.*


class MonthlyReportFragment : BaseFragment<FragmentMonthlyReportBinding>(R.layout.fragment_monthly_report) {
    private val report2ViewModel by activityViewModels<Report2ViewModel>()
    private var currentSelectDay = System.currentTimeMillis()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setView()
        bindView()
    }

    private fun setView() {
        with(binding) {
            vm = report2ViewModel
            previousButton.setOnClickListener {
                compactCalendarView.scrollLeft()
                report2ViewModel.getStepReportFromMonth(-1)
                gaSendLogEvent(requireContext(), "report_prev_period_btn_tap")
            }
            nextButton.setOnClickListener {
                compactCalendarView.scrollRight()
                report2ViewModel.getStepReportFromMonth(1)
                gaSendLogEvent(requireContext(), "report_next_period_btn_tap")
            }
            compactCalendarView.run {
                shouldScrollMonth(false)
                setFirstDayOfWeek(Calendar.SUNDAY)
                setEventIndicatorStyle(1)
            }
            compactCalendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
                override fun onDayClick(dateClicked: Date) {
                    if (dateClicked.after(Date())) {
                        currentSelectDay = 0L
                        report2ViewModel.unSelectStepReport()
                        compactCalendarView.setCurrentSelectedDayBackgroundColor(requireContext().getColor(R.color.white))
                        compactCalendarView.setCurrentSelectedDayTextColor(requireContext().getColor(R.color.sub))
                        return
                    }
                    val events: List<Event> = compactCalendarView.getEvents(dateClicked)
                    if (events.isNotEmpty()) {
                        val data = events[0].data as ReportWalkResponse
                        report2ViewModel.replaceSelectStepReport(data.dailySteps, data.dailyDonationSteps)
                    } else {
                        report2ViewModel.replaceSelectStepReport(0, 0)
                    }
                    if (currentSelectDay == dateClicked.time) {
                        report2ViewModel.unSelectStepReport()
                        currentSelectDay = 0L
                    } else {
                        gaSendLogEvent(requireContext(), "report_selected_date_databox_view")
                        report2ViewModel.selectStepReport()
                        compactCalendarView.setCurrentSelectedDayBackgroundColor(requireContext().getColor(R.color.theme_174dfe))
                        compactCalendarView.setCurrentSelectedDayTextColor(requireContext().getColor(R.color.white))
                        currentSelectDay = dateClicked.time
                    }
                }

                override fun onMonthScroll(firstDayOfNewMonth: Date) {
                    Log.d("TAG", "Month was scrolled to: $firstDayOfNewMonth")
//                    report2ViewModel.getStepReportFromMonth()
                }
            })
        }
    }

    fun deselectDateCheck(dateClicked: Date) {
        val events: List<Event> = binding.compactCalendarView.getEvents(dateClicked)
        var bgColor: Int = requireContext().getColor(R.color.white)
        var textColor: Int = requireContext().getColor(R.color.sub)
        if (events.isNotEmpty()) {
            val data = events[0].data as ReportWalkResponse
            when {
                data.dailyDonationSteps in 1..4999 -> {
                    bgColor = Color.parseColor("#26174cf5")
                }
                data.dailyDonationSteps in 5000..9999 -> {
                    bgColor = Color.parseColor("#66174cf5")
                }
                data.dailyDonationSteps >= 10000 -> {
                    bgColor = Color.parseColor("#a6174cf5")
                }
            }
        }
        val currentDay = SimpleDateFormat("dd").format(Date())
        val selectDay = SimpleDateFormat("dd").format(dateClicked)
        if (currentDay == selectDay) textColor = requireContext().getColor(R.color.theme_174dfe)
        binding.compactCalendarView.setCurrentSelectedDayBackgroundColor(bgColor)
        binding.compactCalendarView.setCurrentSelectedDayTextColor(textColor)
    }

    private fun setCalendar(dayOfMonthReportResponse: DayOfMonthReportResponse) {
        with(binding) {
            dayOfMonthReportResponse.reportWalks?.forEachIndexed { index, walkData ->
                val currentDay = SimpleDateFormat("dd").format(Date())
                val selectDay = SimpleDateFormat("dd").format(walkData.dateToTimeInMillis())
                var colorString = "#ffffff"
                when {
                    walkData.dailyDonationSteps == 0 -> {
                        compactCalendarView.addEvent(Event(Color.parseColor("#ffffff"), walkData.dateToTimeInMillis(), walkData))
                        colorString = "#ffffff"
                    }
                    walkData.dailyDonationSteps in 1..4999 -> {
                        compactCalendarView.addEvent(Event(Color.parseColor("#26174cf5"), walkData.dateToTimeInMillis(), walkData))
                        colorString = "#26174cf5"
                    }
                    walkData.dailyDonationSteps in 5000..9999 -> {
                        compactCalendarView.addEvent(Event(Color.parseColor("#66174cf5"), walkData.dateToTimeInMillis(), walkData))
                        colorString = "#66174cf5"
                    }
                    walkData.dailyDonationSteps >= 10000 -> {
                        compactCalendarView.addEvent(Event(Color.parseColor("#a6174cf5"), walkData.dateToTimeInMillis(), walkData))
                        colorString = "#a6174cf5"
                    }
                }
                if (currentDay == selectDay) compactCalendarView.setCurrentDayBackgroundColor(Color.parseColor(colorString))
            }
        }
    }

    private fun bindView() {
        with(report2ViewModel) {
            report2ViewModel.getStepReportFromMonth(0)
            dayOfMonthReportResponse.observe(viewLifecycleOwner, Observer {
                setCalendar(it)
            })
            isShowingSelectData.observe(viewLifecycleOwner, Observer {
                if (!it && currentSelectDay != 0L) deselectDateCheck(Date(currentSelectDay))
            })
        }
    }

    companion object {
        fun newInstance() =
            MonthlyReportFragment()
    }
}