package kr.co.bigwalk.app.report

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.tabs.TabLayout
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.data.report.dto.DonationRatioResponse
import kr.co.bigwalk.app.databinding.ActivityReport2Binding
import kr.co.bigwalk.app.extension.getDeviceHeight
import kr.co.bigwalk.app.report.monthly.MonthlyReportFragment
import kr.co.bigwalk.app.report.weekly.WeeklyReportFragment
import kr.co.bigwalk.app.util.gaSendLogEvent
import kr.co.bigwalk.app.util.showToast


class Report2Activity : BaseActivity<ActivityReport2Binding>(R.layout.activity_report2) {

    private val report2ViewModel by viewModels<Report2ViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gaSendLogEvent(this, "report_view")
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.chart_data_container,
                WeeklyReportFragment.newInstance(),
                resources.getString(R.string.weekly)
            )
            .commit()

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_exit)
            title = "리포트"
        }
    }

    private fun setView() {
        with(binding) {
            vm = report2ViewModel
            periodTab.run {
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab ?: return

                        val findFragment = supportFragmentManager.findFragmentByTag("${tab.text}")
                        if (findFragment == null) {
                            supportFragmentManager.beginTransaction()
                                .add(R.id.chart_data_container, getFragment(tab.position), "${tab.text}")
                                .commit()
                        } else {
                            supportFragmentManager.beginTransaction()
                                .show(findFragment)
                                .commit()
                        }
                        report2ViewModel.changeDataByPeriodShown(tab.position)
                        when(tab.position) {
                            0 -> gaSendLogEvent(this@Report2Activity, "report_week_btn_tap")
                            1 -> gaSendLogEvent(this@Report2Activity, "report_month_btn_tap")
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        tab ?: return
                        val hideFragment = supportFragmentManager.findFragmentByTag("${tab.text}")
                        if (hideFragment != null) {
                            supportFragmentManager.beginTransaction()
                                .hide(hideFragment)
                                .commit()
                        }
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }

                })
            }
            selectStepData.setOnClickListener {
                report2ViewModel.unSelectStepReport()
            }
            var isPlay = false
            var isSend = false
            scrollContainer.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                val scrollPositionOfBottom = getDeviceHeight() - toolbar.height + scrollY
                val position = (carbonEffect.root.top + carbonEffect.root.bottom) / 2
                if (scrollPositionOfBottom >= position && isPlay.not()) {
                    isPlay = true
                    report2ViewModel.setStartGif(true)
                }
                if (v?.canScrollVertically(1) != true && !isSend) {
                    gaSendLogEvent(this@Report2Activity, "report_scroll_to_bottom_event")
                    isSend = true
                }
            }
            energyEffect.image.setOnClickListener { gaSendLogEvent(this@Report2Activity, "report_walk_effect_icon_tap") }
            carbonEffect.image.setOnClickListener { gaSendLogEvent(this@Report2Activity, "report_walk_effect_icon_tap") }

        }

        //collecting the entries with label name
        val pieDataSet = PieDataSet(listOf(PieEntry(1f, "empty")), "type")
        //setting text size of the value
        pieDataSet.valueTextSize = 0f
        //providing color list for coloring different entries
        pieDataSet.color = getColor(R.color.background_grey)
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true)
        binding.pieChart.run {
            description.isEnabled = false
            isRotationEnabled = false
            setTouchEnabled(false)
            legend.isEnabled = false
            setEntryLabelTextSize(0f)
        }
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
        binding.pieChart.setTransparentCircleAlpha(0)
        binding.pieChart.setOnClickListener { gaSendLogEvent(this@Report2Activity, "report_donation_propensity_pie_chart_tap") }
    }

    private fun getFragment(position: Int): Fragment {
        return when (position) {
            0 -> WeeklyReportFragment.newInstance()
            1 -> MonthlyReportFragment.newInstance()
            else -> error("Invalid position")
        }
    }

    private fun showPieChart(donationRatioResponse: DonationRatioResponse) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = "type"

        //initializing data
        val typeAmountMap: MutableMap<String, Double> = HashMap()
        typeAmountMap[ReportDonationCategory.ENVIRONMENT.type] = donationRatioResponse.environmentDonationRatio
        typeAmountMap[ReportDonationCategory.ANIMAL.type] = donationRatioResponse.animalDonationRatio
        typeAmountMap[ReportDonationCategory.CHILD.type] = donationRatioResponse.childDonationRatio
        typeAmountMap[ReportDonationCategory.DISABLED.type] = donationRatioResponse.disabledDonationRatio
        typeAmountMap[ReportDonationCategory.OLDMAN.type] = donationRatioResponse.oldManDonationRatio
        typeAmountMap[ReportDonationCategory.GLOBAL.type] = donationRatioResponse.globalDonationRatio

        val maxType = (typeAmountMap.values.maxOrNull() ?: 0.0).toInt()
        val test: String = typeAmountMap.keys.find { typeAmountMap[it]?.toInt() == maxType }.orEmpty()
        when(test) {
            ReportDonationCategory.ENVIRONMENT.type -> {
                binding.tendencyIcon.setImageResource(R.drawable.report_category_environment)
                binding.tendencyTitle.text = ReportDonationCategory.ENVIRONMENT.title
                binding.tendencySubTitle.text = "${ReportDonationCategory.ENVIRONMENT.subTitle} 관련 캠페인에 $maxType% 기부했어요!"
            }
            ReportDonationCategory.ANIMAL.type -> {
                binding.tendencyIcon.setImageResource(R.drawable.report_category_animal)
                binding.tendencyTitle.text = ReportDonationCategory.ANIMAL.title
                binding.tendencySubTitle.text = "${ReportDonationCategory.ANIMAL.subTitle} 관련 캠페인에 $maxType% 기부했어요!"
            }
            ReportDonationCategory.CHILD.type -> {
                binding.tendencyIcon.setImageResource(R.drawable.report_category_child)
                binding.tendencyTitle.text = ReportDonationCategory.CHILD.title
                binding.tendencySubTitle.text = "${ReportDonationCategory.CHILD.subTitle} 관련 캠페인에 $maxType% 기부했어요!"
            }
            ReportDonationCategory.DISABLED.type -> {
                binding.tendencyIcon.setImageResource(R.drawable.report_category_disabled)
                binding.tendencyTitle.text = ReportDonationCategory.DISABLED.title
                binding.tendencySubTitle.text = "${ReportDonationCategory.DISABLED.subTitle} 관련 캠페인에 $maxType% 기부했어요!"
            }
            ReportDonationCategory.OLDMAN.type -> {
                binding.tendencyIcon.setImageResource(R.drawable.report_category_senile)
                binding.tendencyTitle.text = ReportDonationCategory.OLDMAN.title
                binding.tendencySubTitle.text = "${ReportDonationCategory.OLDMAN.subTitle} 관련 캠페인에 $maxType% 기부했어요!"
            }
            ReportDonationCategory.GLOBAL.type -> {
                binding.tendencyIcon.setImageResource(R.drawable.report_category_global)
                binding.tendencyTitle.text = ReportDonationCategory.GLOBAL.title
                binding.tendencySubTitle.text = "${ReportDonationCategory.GLOBAL.subTitle} 관련 캠페인에 $maxType% 기부했어요!"
            }
        }
        binding.tendencyIcon.isInvisible = maxType <= 0.0
        binding.tendencyTitle.isInvisible = maxType <= 0.0
        binding.tendencySubTitle.isInvisible = maxType <= 0.0
        binding.tendencyEmptyView.isVisible = maxType <= 0.0
        if (maxType <= 0.0) return // 모든 차트의 비율이 0일 때 데이터 삽입하지 않음.


        //initializing colors for the entries
        val colors: ArrayList<Int> = ArrayList()
        colors.add(getColor(R.color.emerald_green))
        colors.add(getColor(R.color.blue))
        colors.add(getColor(R.color.maize))
        colors.add(Color.parseColor("#fc7987"))
        colors.add(getColor(R.color.groove_purple))
        colors.add(getColor(R.color.pacific_blue))

        //input data and fit data into pie chart entry
        pieEntries.add(PieEntry(typeAmountMap[ReportDonationCategory.ENVIRONMENT.type]!!.toFloat(), ReportDonationCategory.ENVIRONMENT.type))
        pieEntries.add(PieEntry(typeAmountMap[ReportDonationCategory.ANIMAL.type]!!.toFloat(), ReportDonationCategory.ANIMAL.type))
        pieEntries.add(PieEntry(typeAmountMap[ReportDonationCategory.CHILD.type]!!.toFloat(), ReportDonationCategory.CHILD.type))
        pieEntries.add(PieEntry(typeAmountMap[ReportDonationCategory.DISABLED.type]!!.toFloat(), ReportDonationCategory.DISABLED.type))
        pieEntries.add(PieEntry(typeAmountMap[ReportDonationCategory.OLDMAN.type]!!.toFloat(), ReportDonationCategory.OLDMAN.type))
        pieEntries.add(PieEntry(typeAmountMap[ReportDonationCategory.GLOBAL.type]!!.toFloat(), ReportDonationCategory.GLOBAL.type))

        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label)
        //setting text size of the value
        pieDataSet.valueTextSize = 0f
        //providing color list for coloring different entries
        pieDataSet.colors = colors
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true)
        binding.pieChart.run {
            description.isEnabled = false
            isRotationEnabled = false
            setTouchEnabled(false)
            legend.isEnabled = false
            setEntryLabelTextSize(0f)
        }
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
        binding.pieChart.setTransparentCircleAlpha(0)
    }


    private fun bindViewModel() {
        with(report2ViewModel) {
            donationRatio.observe(this@Report2Activity, Observer {
                showPieChart(it)
            })
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, Report2Activity::class.java)
    }
}

enum class ReportDonationCategory(val type: String, val title: String, val subTitle: String) {
    ENVIRONMENT("environment","빅워크 선정 No. 1 환경 지킴이", "환경"),
    ANIMAL("animal", "동물 친구들의 든든한 수호자", "동물"),
    CHILD("child", "따뜻한 마음의 아이 사랑꾼", "아이"),
    DISABLED("disabled", "밝고 따뜻하고 다정한 봄날의 햇살", "장애인"),
    OLDMAN("oldMan", "꽃보다 할배의 든든한 파트너", "노인"),
    GLOBAL("global", "지구별 지키는 슈퍼히어로", "지구촌")
}