package kr.co.bigwalk.app.report.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FrameDonationReportBinding

class DonationReportFragment: Fragment() {

    lateinit var navigator: BaseNavigator
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var viewModel: DonationReportViewModel

    companion object {
        fun newInstance(navigator: BaseNavigator, firebaseAnalytics: FirebaseAnalytics): DonationReportFragment {
            val fragment = DonationReportFragment()
            fragment.navigator = navigator
            fragment.firebaseAnalytics = firebaseAnalytics
            fragment.viewModel = DonationReportViewModel(navigator)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FrameDonationReportBinding = DataBindingUtil.inflate(inflater, R.layout.frame_donation_report, container, false)
        binding.viewModel = viewModel
        // 탬을 눌러서 뷰가 변경될 때마다 호출됨.
        firebaseAnalytics.logEvent("report_donation_view", Bundle())
        return binding.root
    }
}