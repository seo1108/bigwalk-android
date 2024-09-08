package kr.co.bigwalk.app.campaign.ranking.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.campaign.ranking.RankingPlusViewModel
import kr.co.bigwalk.app.databinding.FrameTotalInfluenceReportBinding

class TotalInfluenceReportFragment(private val viewModel: RankingPlusViewModel): Fragment() {
    companion object {
        fun newInstance(viewModel: RankingPlusViewModel) = TotalInfluenceReportFragment(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FrameTotalInfluenceReportBinding = DataBindingUtil.inflate(inflater, R.layout.frame_total_influence_report, container, false)
        binding.viewModel = viewModel
        return binding.root
    }
}