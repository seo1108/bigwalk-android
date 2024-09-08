package kr.co.bigwalk.app.report.walk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FrameWalkReportBinding
import kr.co.bigwalk.app.report.ReportNavigator
import kr.co.bigwalk.app.report.ReportViewModel


class WalkReportFragment: Fragment() {

    lateinit var navigator: BaseNavigator
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var viewModel: WalkReportViewModel

    companion object {
        fun newInstance(navigator: BaseNavigator, firebaseAnalytics: FirebaseAnalytics): WalkReportFragment {
            val fragment = WalkReportFragment()
            fragment.navigator = navigator
            fragment.firebaseAnalytics = firebaseAnalytics
            fragment.viewModel = WalkReportViewModel(navigator)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FrameWalkReportBinding = DataBindingUtil.inflate(inflater, R.layout.frame_walk_report, container, false)
        binding.viewModel = viewModel
        firebaseAnalytics.logEvent("report_walk_view", Bundle())
        return binding.root
    }

}