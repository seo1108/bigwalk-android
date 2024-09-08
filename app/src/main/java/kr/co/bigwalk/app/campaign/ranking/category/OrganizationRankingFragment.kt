package kr.co.bigwalk.app.campaign.ranking.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.adapter.RankingRecyclerAdapter
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.databinding.FrameOrganizationRankingBinding

class OrganizationRankingFragment: Fragment() {
    companion object {
        fun newInstance() = OrganizationRankingFragment()
    }
    private lateinit var viewModel: OrganizationRankingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseAnalytics.getInstance(requireActivity()).logEvent("ranking_group_view", Bundle())
        val binding: FrameOrganizationRankingBinding = DataBindingUtil.inflate(inflater, R.layout.frame_organization_ranking, container, false)
        viewModel = OrganizationRankingViewModel()
        val adapter = RankingRecyclerAdapter()
        viewModel.ranking.observe(this, Observer<PagedList<RankingResponse>> { pagedList ->
            adapter.submitList(pagedList)
        })
        binding.rankingRecycler.adapter = adapter
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestMyRanking()
        viewModel.invalidateDataSource()
    }
}