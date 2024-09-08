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
import kr.co.bigwalk.app.databinding.FrameMyInfluenceReportBinding

class MyInfluenceReportFragment(private val viewModel: RankingPlusViewModel): Fragment() {
    companion object {
        fun newInstance(viewModel: RankingPlusViewModel) = MyInfluenceReportFragment(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FrameMyInfluenceReportBinding = DataBindingUtil.inflate(inflater, R.layout.frame_my_influence_report, container, false)
        binding.viewModel = viewModel
        return binding.root
    }
}