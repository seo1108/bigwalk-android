package kr.co.bigwalk.app.report

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.report.dto.MonthStatsResponse
import kr.co.bigwalk.app.databinding.ActivityReportBinding
import kr.co.bigwalk.app.report.donation.DonationReportFragment
import kr.co.bigwalk.app.report.walk.WalkReportFragment

class ReportActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        var currentTab = 0
        const val WALK_REPORT = 0
        const val DONATION_REPORT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.logEvent("report_view", Bundle())
        val binding: ActivityReportBinding = DataBindingUtil.setContentView(this, R.layout.activity_report)
        val reportViewModel = ReportViewModel(this, supportFragmentManager)
        binding.viewModel = reportViewModel
        val walkReportFragment = WalkReportFragment.newInstance(this, firebaseAnalytics)
        val donationReportFragment = DonationReportFragment.newInstance(this, firebaseAnalytics)
        supportFragmentManager.beginTransaction().replace(R.id.report_list_frame, walkReportFragment).commit()
        binding.reportTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(p0: TabLayout.Tab?) {
                val position = p0?.position
                if (position == 0) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.report_list_frame, walkReportFragment)
                        .commit()
                    currentTab = WALK_REPORT
                    firebaseAnalytics.logEvent("report_walk_tab_button", Bundle())
                } else if (position == 1) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.report_list_frame, donationReportFragment)
                        .commit()
                    currentTab = DONATION_REPORT
                    firebaseAnalytics.logEvent("report_donation_tab_button", Bundle())
                }
            }

        })
        //프레그먼트는 액티비티에서 생성해서 가지고 있음.
        //미리 생성해두고 버튼을 누를 때마다 뷰만 변경해서 보여주는 방식.
        //단, 현재 oncreateview에서 데이터를 호출하다보니 뷰만 변경되는 것이 아니라, 데이터도 매번 호출된다.
        //이외에 날짜탭을 선택할 때마다 다르게 리포트를 호출해주어야 한다.
        //그리고 리포트를 호출할 때, 그 기간 단위로 관련된 모든 뷰를 한번에 보여주어야 한다.
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onBackPressed() {
        finish()
    }
}